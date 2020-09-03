<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
  <head>
    <meta charset="UTF-8">
    <link href="/resources/css/style.css" type="text/css" rel="stylesheet">
  </head>

  <body>
    <div class="container">

  	  <h1>Demo Service Provider</h1>
      <p>OP Identity Service Broker allows Service Providers (you) to implement strong electronic identification (Finnish bank credentials, Mobile ID) easily to websites and mobile apps via single API.</p>
      <p>This Demo Service Provider gives you three different examples how to integrate to the Identification Service Broker:</p>
      <ol>
          <li>
              <a href="/">OP’s hosted identification UI</a>
          </li>
          <li>
              Self-hosted UI (buttons) embedded into your service
          </li>
      </ol>
      <form class="example" action="initFlow" id="initFlow">
       	<h2>Self-hosted UI (buttons) embedded into your service</h2>
       	<div class="row">
            <div class="info">
                <p>Alternatively you can embed the indentification UI into your service and render it as you like. The end-user is redirected straight from your UI to the selected Identity Provider (bank, Mobile ID) instead of OP&#x27;s hosted UI.</p>
                <p>After identifying herself the end-user is redirected back to the return_uri you specify in the authentication request. The consent can be set in the request as well.</p>
                <p><a href="https://github.com/op-developer/Identity-Service-Broker-API/blob/master/README.md" target="_blank">See the API docs for more information.</a></p> 
           </div>
            <section class="params">
              <h3>Parameters for the identification request:</h3>
              <ul class="param-group">
                <li>
                  <input type="checkbox" id="promptBox" name="promptBox" value="consent">
                  <label for="promptBox">
                    Require consent
                    <br/>
                    <small>End-user is forced to review her personal data before returning to service.</small>
                  </label>
                </li>
              </ul>
              <ul class="param-group">
                <li>
                  <input type="radio" name="purpose" value="normal" id="idBasic" checked>
                  <label for="idBasic">Basic identification</label>
                </li>
            
                <li>
                  <input type="radio" name="purpose" value="weak" id="idWeak">
                  <label for="idWeak">New weak credentials</label>
                </li>
            
                <li>
                  <input type="radio" name="purpose" value="strong" id="idStrong">
                  <label for="idStrong">New strong credentials</label>
                </li>
                               
              </ul>
              
             <h5>User Interaface Language</h5>
             
             <% String language = (String) request.getSession().getAttribute("language"); %>
             
			<script>
			function autoSubmit()
			{
			    var formObject = document.forms['initFlow'];
			    formObject.submit();
			}
			</script>   
			          
            <ul class="param-group">
              <li>
                <input type="radio" name="language" onChange="autoSubmit();" value="fi" id="language_fi" <% if (language.equals("fi")) out.write("checked"); %>  >
                <label for="language_fi">Finnish</label>
              </li>
              <li>
                <input type="radio" name="language" onChange="autoSubmit();" value="sv" id="language_sv" <% if (language.equals("sv")) out.write("checked"); %>  >
                <label for="language_sv">Swedish</label>
              </li>
              <li>
                <input type="radio" name="language" onChange="autoSubmit();" value="en" id="language_en" <% if (language.equals("en")) out.write("checked"); %>  >
                <label for="language_en">English</label>
              </li>           
              
            </ul>             
              
            </section>
        </div>
        
  		<div class="note">
    		<p>Note that it’s mandatory to display the following texts in the UI even if you embed it into you service:</p>
    		<ul>
        		<li>${isbConsent}</li>
        		<li>${isbProviderInfo}</li>
    		</ul>
		</div>        

        <h3 class="view-title">
            
              Example UI 2: Embedded buttons
        </h3>
 
        <div class="view">
          <div class="view-layout">
            <div class="view-main">
			   <p>${isbConsent}</p>
			   <%
			   		if (((String)(request.getSession().getAttribute("disturbanceinfo"))).equals("yes")) {
			   			
			   %>
               <div class="alert -info">
                    <h3 class="disturbanceTitle"> ${disturbanceInfo.header } </h3>
                    <div class="disturbanceMessage"> ${disturbanceInfo.text } </div>
                </div>
                
               <%
               		}
               %> 
                
              <div class="idp-buttons">
                <c:forEach var = "i" items="${identityProviders}">
                  <div class="idp-button">
                    <button type="submit" name="idp" value="${i.ftnIdpId}" id="${i.ftnIdpId}" class="id-button">
                      <img src="${i.imageUrl}" alt="idp logo" alt="${i.name}"/>
                      <!--  <p>${i.name}</p> -->
                    </button>
                  </div>
                </c:forEach>
              </div>
               <p>${isbProviderInfo}</p>
            </div>
          </div>
        </div>
      </form>
    </div>
  </body>
</html>
