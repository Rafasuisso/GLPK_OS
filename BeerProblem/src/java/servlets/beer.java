/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import org.gnu.glpk.*;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Lucas
 */
@WebServlet(name = "beer", urlPatterns = {"/beer"})
public class beer extends HttpServlet {
    
    //Global Variables
    glp_prob mip;
    glp_smcp parm;
    SWIGTYPE_p_int index;
    SWIGTYPE_p_double value;
    int answer;
    int variableCount = 6;
    int constraintCount = 5;
    
    
    //Variables of Products 
    double ipa, pilsen, weissbier, lager, stout, dubbel;
    
    //Variables of Feedstock
    double malte, lupulo, levedura, trigo, milho;
    
    //Matrix of Constraints
    double[][] constraintList = {{90, 60, 45, 60, 100, 180, 0, 0, 0, 0, 0},
                                 {28, 23, 29, 21, 30, 35, 0, 0, 0, 0, 0},
                                 {28, 20, 70, 20, 23, 25, 0, 0, 0, 0, 0},
                                 {0, 0, 80, 0, 0, 0, 0, 0, 0, 0, 0},
                                 {0, 0, 0, 60, 0, 0, 0, 0, 0, 0, 0}};
    
    //List of Variables of Problem
    String[] varList = {"x1", "x2", "x3", "x4", "x5", "x6"}; 

    //List of bounds
    double[] bounds;
    
    //List of coefficients of products
    double[] coef;
    
    //Metodo responsavel por inicializar as variaveis
    public void initialize(double[] bier, double[] feedstock){
        //Initialize of user data
        bounds = feedstock;
        coef = bier;
        
        startGLPK();
    }
    
    //Metodo que inicia o GLPK
    public void startGLPK(){
        try{
            // Create the problem on GLPK
            mip = GLPK.glp_create_prob();
            System.out.println("Problem created!");
            GLPK.glp_set_prob_name(mip, "Mixed Integer Production - Beer");
            
            //Define columns on GLPK
            GLPK.glp_add_cols(mip, variableCount);
            
            
            //Initial set up variables in GLPK
            for(int x = 0; x < varList.length; x++){
                GLPK.glp_set_col_name(mip, x+1, varList[x]);
                GLPK.glp_set_col_kind(mip, x+1, GLPKConstants.GLP_IV);
                GLPK.glp_set_col_bnds(mip, x+1, GLPKConstants.GLP_LO, 0, 0);
            }
            
            //Allocate memory in the simulate C 
            index = GLPK.new_intArray(variableCount);
            value = GLPK.new_doubleArray(variableCount);
            
            //Create rows
            GLPK.glp_add_rows(mip, constraintCount);
            
            //Set row details
            for(int x = 1; x <= constraintCount; x++){
                GLPK.glp_set_row_name(mip, x, "C"+x);
                GLPK.glp_set_row_bnds(mip, x, GLPKConstants.GLP_UP, 0, bounds[x-1]);
                
                for(int y = 1; y <= variableCount; y++){
                    GLPK.intArray_setitem(index, y, y);
                }
                
                for(int y = 1; y <= variableCount; y++){
                    GLPK.doubleArray_setitem(value, y, constraintList[x-1][y-1]);
                }
                
                GLPK.glp_set_mat_row(mip, x, variableCount, index, value);
            }
            
            // Erase memory of index and value
            GLPK.delete_intArray(index);
            GLPK.delete_doubleArray(value);
            
            // Define Objective Function
            GLPK.glp_set_obj_name(mip, "Z");
            GLPK.glp_set_obj_dir(mip, GLPKConstants.GLP_MAX);
            GLPK.glp_set_obj_coef(mip, 0, 0);
            
            for(int x = 0; x < variableCount; x++){
                GLPK.glp_set_obj_coef(mip, x+1, coef[x]);
            }
            
            // Print model in the file format of glpk
            GLPK.glp_write_lp(mip, null, "beer.lp");
            
            // Solve the model
            parm = new glp_smcp();
            GLPK.glp_init_smcp(parm);
            answer = GLPK.glp_simplex(mip, parm);
            
        }catch (GlpkException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Retrieved by requisition method POST
        //Price list of beers
        double[] bier = new double[variableCount];
                
        bier[0] = Double.parseDouble(request.getParameter("ipa"));
        bier[1] = Double.parseDouble(request.getParameter("pilsen"));
        bier[2] = Double.parseDouble(request.getParameter("weissbier"));
        bier[3] = Double.parseDouble(request.getParameter("lager"));
        bier[4] = Double.parseDouble(request.getParameter("stout"));
        bier[5] = Double.parseDouble(request.getParameter("dubbel"));
        
        //Stock definition of feedstock
        double[] feedstock = new double[constraintCount];
                
        feedstock[0] = Double.parseDouble(request.getParameter("malte"));
        feedstock[1] = Double.parseDouble(request.getParameter("lupulo"));
        feedstock[2] = Double.parseDouble(request.getParameter("levedura"));
        feedstock[3] = Double.parseDouble(request.getParameter("trigo"));
        feedstock[4] = Double.parseDouble(request.getParameter("milho"));
        
        
        initialize(bier, feedstock);
        
        if(answer == 0){
            
                double[] qtdByBier = new double[7];
                qtdByBier[0] = GLPK.glp_get_obj_val(mip);
                
                for (int i = 1; i <= GLPK.glp_get_num_cols(mip); i++) {
                    qtdByBier[i] = GLPK.glp_get_col_prim(mip, i);
                }
                
                request.setAttribute("z", qtdByBier[0]);
                request.setAttribute("x1", qtdByBier[1]);
                request.setAttribute("x2", qtdByBier[2]);
                request.setAttribute("x3", qtdByBier[3]);
                request.setAttribute("x4", qtdByBier[4]);
                request.setAttribute("x5", qtdByBier[5]);
                request.setAttribute("x6", qtdByBier[6]);
                
                request.getRequestDispatcher("sucess.jsp").forward(request, response);
        } else {
            System.out.println("Oh no! :( - Problem do not have solution!");
        }
    }

   
}
