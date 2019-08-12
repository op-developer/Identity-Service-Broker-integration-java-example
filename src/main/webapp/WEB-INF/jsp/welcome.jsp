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
        <h1>OIDC Identity Broker Integration Example</h1>

        <h2>Self-hosted UI (buttons) embedded into your service</h2>
        <div class="row">
          <div class="info">
            <p>Alternatively you can embed the indentification UI into your service and render it as you like. The end-user is redirected straight from your UI to the selected Identity Provider (bank, Mobile ID) instead of OP&#x27;s hosted UI.</p>
            <p>After identifying herself the end-user is redirected back to the return_uri you specify in the authentication request. The consent can be set in the request as well.</p>
            <p><a href="#">See the API docs for more information.</a></p>
            <p>Note that it’s mandatory to display the following texts in the UI even if you embed it into you service:</p>
            <ul>
              <li>By continuing, I accept that the service provider will receive my name and personal identity code.</li>
              <li>Identification is provided by OP-Palvelut Oy.</li>
            </ul>
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
            <h5>Purpose</h5>
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

        <p>Tunnistautumalla seuraavilla tunnistavoilla hyväksyt, että palveluntarjoajalle välitetään: nimi ja henkilötunnus.</p>

        <h3 class="view-title">
            Example UI
                1: Embedded buttons
        </h3>

 
        <div class="view">
          <div class="view-layout">
            <div class="view-main">
              <p>By using the following means of authentication you accept that your personal ID code and name will be transmitted to the service provider.</p>
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
            </div>
          </div>
        </div>

        <h3 class="view-title">
          Example UI
            2: OP-hosted buttons
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
