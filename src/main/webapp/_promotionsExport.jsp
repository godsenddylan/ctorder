<%@ page contentType="application/vnd.ms-excel; charset=UTF-8" %>  
<%  
    response.addHeader("Content-Disposition", "filename=promotions.xls");  
	response.setCharacterEncoding("utf-8");
%> 


<table width="100%" border="1" cellspacing="1">
      <tr>
	       <td>订单号</td>
      </tr>
      <tr>
      	 <td>
      	 	${ooo}
      	 </td>
      </tr>
</table>
