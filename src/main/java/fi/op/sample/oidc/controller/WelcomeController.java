package fi.op.sample.oidc.controller;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fi.op.sample.oidc.domain.Identity;
import fi.op.sample.oidc.domain.IdentityProviderListBuilder;
import fi.op.sample.oidc.domain.OidcRequestParameters;
import fi.op.sample.oidc.domain.OidcResponseParameters;
import fi.op.sample.oidc.domain.idp.IdentityProvider;
import fi.op.sample.oidc.domain.idp.IdentityProviderList;
import fi.op.sample.oidc.facade.OidcDemoFacade;
import net.minidev.json.JSONObject;

@Controller
public class WelcomeController {
    private final Logger logger = LoggerFactory.getLogger(WelcomeController.class);

    private OidcDemoFacade facade;

    
    @RequestMapping("/")
    public String welcome(HttpServletRequest request, Map<String, Object> model) throws UnsupportedEncodingException {
        
    	request.getSession().setAttribute("backurlprefix", "");
    	
        return "welcome";
    }   
    
    @RequestMapping("/embedded")
    public String embedded(HttpServletRequest request, Map<String, Object> model) throws UnsupportedEncodingException {
           	
    	String language = request.getParameter("language");
    	if (language==null) language="fi"; // Default language is fi
    	
    	IdentityProviderList idpList = new IdentityProviderListBuilder(null).build(language);
    	List<IdentityProvider> idps = idpList.getIdentityProviders();
        model.put("identityProviders", idps);
        JSONObject disturbanceInfo = idpList.getDisturbanceInfo();
        model.put("disturbanceInfo", disturbanceInfo);
        String isbProviderInfo = idpList.getIsbProviderInfo();
        model.put("isbProviderInfo", isbProviderInfo);
        String isbConcent = idpList.getIsbConsent();
        model.put("isbConsent", isbConcent);
               
        // But default GUI language, and GUI type to session (for render) 
        request.getSession().setAttribute("language", "fi"); // Default language is fi
        request.getSession().setAttribute("backurlprefix", "embedded");
        
        return "embedded";
    }

    @RequestMapping("/initFlow")
    public String initFlow(HttpServletRequest request, Map<String, Object> model) {

        String language = request.getParameter("language");
        String idp = request.getParameter("idp");
        String requestId = UUID.randomUUID().toString();
        String promptParam = request.getParameter("promptBox");
        String purpose = request.getParameter("purpose");
             
        // If embedded initiated authentication started and No IdP parameter in request -> Refresh GUI button pressed
        // Lets get IdP list and render embedded GUI with selected language
        
        if (idp==null && request.getSession().getAttribute("backurlprefix").equals("embedded")) {
            request.getSession().setAttribute("language", language);
        	IdentityProviderList idpList = new IdentityProviderListBuilder(null).build(language);
        	List<IdentityProvider> idps = idpList.getIdentityProviders();
            model.put("identityProviders", idps);
            JSONObject disturbanceInfo = idpList.getDisturbanceInfo();
            model.put("disturbanceInfo", disturbanceInfo);
            String isbProviderInfo = idpList.getIsbProviderInfo();
            model.put("isbProviderInfo", isbProviderInfo);
            String isbConcent = idpList.getIsbConsent();
            model.put("isbConsent", isbConcent);           
            request.getSession().setAttribute("backurlprefix", "embedded");
            return "embedded";       	       	
        }
        
        boolean prompt = promptParam != null && promptParam.equals("consent");
        OidcRequestParameters params = getFacade().oidcAuthMessage(idp, language, requestId, prompt, purpose);
        logger.info("Request: {}", params.getRequest());
        model.put("endpointUrl", params.getEndpointUrl());
        model.put("request", params.getRequest());
        request.getSession().setAttribute("initParams", params);
        return "post";
    }

    @RequestMapping("/finishFlow")
    public String finishFlow(HttpServletRequest request, Map<String, Object> model) {
        OidcRequestParameters originalParams = (OidcRequestParameters) request.getSession().getAttribute("initParams");
        OidcResponseParameters response = new OidcResponseParameters();
        response.setError(request.getParameter("error"));
        response.setState(request.getParameter("state"));
        response.setCode(request.getParameter("code"));
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm:ss");  
        LocalDateTime now = LocalDateTime.now();  
        String timenow = dtf.format(now);
        
        if (response.getError() == null || response.getError().length() == 0) {
            Identity identity = getFacade().extractIdentity(response, originalParams);
            model.put ("timenow", timenow);
            model.put("identity", identity);
            model.put("backurlprefix", request.getSession().getAttribute("backUrlPost"));
            return "identity";
        } 
        else if (response.getError().equals("cancel")) {
        	// If embedded GUI used to initiate authentication and it has been canceled lets render 
        	// it again. The list of IdPs must be collected from ISB. Selected language can be found 
        	// from session
        	if (request.getSession().getAttribute("backurlprefix").equals("embedded")) {
                String language = (String) request.getSession().getAttribute("language");
            	IdentityProviderList idpList = new IdentityProviderListBuilder(null).build(language);
            	List<IdentityProvider> idps = idpList.getIdentityProviders();
                model.put("identityProviders", idps);
                JSONObject disturbanceInfo = idpList.getDisturbanceInfo();
                model.put("disturbanceInfo", disturbanceInfo);
                String isbProviderInfo = idpList.getIsbProviderInfo();
                model.put("isbProviderInfo", isbProviderInfo);
                String isbConcent = idpList.getIsbConsent();
                model.put("isbConsent", isbConcent);           
                request.getSession().setAttribute("backurlprefix", "embedded");       		
        		return "embedded";
        	}
        	else {
        		return "welcome";
        	}
        } 
        else {
            model.put("error", response.getError());
            return "error";
        }
    }

    @RequestMapping(method = { RequestMethod.GET }, value = "/jwks", produces = "application/json; charset=utf-8")
    @ResponseBody
    public String jwks() {
        return getFacade().getJwks();
    }

    private OidcDemoFacade getFacade() {
        if (facade == null) {
            facade = new OidcDemoFacade();
        }
        return facade;
    }
}
