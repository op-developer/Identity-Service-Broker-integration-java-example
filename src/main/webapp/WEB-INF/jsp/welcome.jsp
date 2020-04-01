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
      <form class="example" action="initFlow">

	    <h1>Demo Service Provider</h1>
        <p>OP Identity Service Broker allows Service Providers (you) to implement strong electronic identification (Finnish bank credentials, Mobile ID) easily to websites and mobile apps via single API.</p>
        <p>This Demo Service Provider gives you three different examples how to integrate to the Identification Service Broker:</p>
        <ol>
            <li>
                OP’s hosted identification UI
            </li>
            <li>
                <a href="embedded">Self-hosted UI (buttons) embedded into your service</a>
            </li>
        </ol>
		<h2>OP’s hosted identification UI</h2>
        <div class="row">
            <div class="info">
                <p>You can place a button or link or some other call-to-action into your UI which redirects the end-user to OP’s hosted identification UI along with the Open ID Connect authentication request.</p>
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
            <ul class="param-group">
              <li>
                <input type="radio" name="language" value="fi" id="language_fi" checked>
                <label for="language_fi">Finnish</label>
              </li>
              <li>
                <input type="radio" name="language" value="sv" id="language_sv">
                <label for="language_sv">Swedish</label>
              </li>
              <li>
                <input type="radio" name="language" value="en" id="language_en">
                <label for="language_en">English</label>
              </li>
            </ul>
              
              
            </section>
        </div>
 
        <h3 class="view-title">
          
            Example UI 1: Hosted UI
        </h3>

        <div class="view">
          <div class="view-layout">
            <div class="view-main">
              <button type="submit" class="button" value="Submit">Identify yourself</button>
            </div>
          </div>
        </div>
      </form>
    </div>
  </body>
</html>
