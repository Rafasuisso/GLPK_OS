<%-- 
    Document   : sucess
    Created on : 14/11/2017, 06:08:30
    Author     : Lucas
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="assets/bootstrap.css" type="text/css" rel="stylesheet">
        <title>Success! :)</title>
    </head>
    <body>
        <div class="container">
            <div class="row">
                <h1>Resultado do GLPK (Problema da Cerveja)</h1>
            </div>
            <div class="row">
                <div class="col-md-6 offset-3">
                    <form>
                        <div class="form-group">
                            <label>Z</label>
                            <input type="text" class="form-control" value="<%= request.getAttribute("z")%>">
                        </div>
                        <div class="form-group">
                            <label>X1</label>
                            <input type="text" class="form-control" value="<%= request.getAttribute("x1")%>">
                        </div>
                        <div class="form-group">
                            <label>X2</label>
                            <input type="text" class="form-control" value="<%= request.getAttribute("x2")%>">
                        </div>
                        <div class="form-group">
                            <label>X3</label>
                            <input type="text" class="form-control" value="<%= request.getAttribute("x3")%>">
                        </div>
                        <div class="form-group">
                            <label>X4</label>
                            <input type="text" class="form-control" value="<%= request.getAttribute("x4")%>">
                        </div>
                        <div class="form-group">
                            <label>X5</label>
                            <input type="text" class="form-control" value="<%= request.getAttribute("x5")%>">
                        </div>
                        <div class="form-group">
                            <label>X6</label>
                            <input type="text" class="form-control" value="<%= request.getAttribute("x6")%>">
                        </div>
                        <hr>
                        <input type="button" class="btn btn-primary" value="Voltar" onclick="history.back()">
                    </form>
                </div>
            </div>
        </div>
    </body>
</html>
