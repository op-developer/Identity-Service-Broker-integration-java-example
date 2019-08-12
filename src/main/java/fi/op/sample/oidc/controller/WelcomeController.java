package fi.op.sample.oidc.controller;

import java.io.UnsupportedEncodingException;
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
import fi.op.sample.oidc.facade.OidcDemoFacade;

@Controller
public class WelcomeController {
    private final Logger logger = LoggerFactory.getLogger(WelcomeController.class);

    private OidcDemoFacade facade;

    @RequestMapping("/")
    public String welcome(HttpServletRequest request, Map<String, Object> model) throws UnsupportedEncodingException {
        List<IdentityProvider> idps = new IdentityProviderListBuilder(null).build().getIdentityProviders();
        model.put("identityProviders", idps);
        return "welcome";
    }

    @RequestMapping("/initFlow")
    public String initFlow(HttpServletRequest request, Map<String, Object> model) {

        String language = request.getParameter("language");
        String idp = request.getParameter("idp");
        String requestId = UUID.randomUUID().toString();
        String promptParam = request.getParameter("promptBox");
        String purpose = request.getParameter("purpose");
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
        if (response.getError() == null || response.getError().length() == 0) {
            Identity identity = getFacade().extractIdentity(response, originalParams);
            model.put("identity", identity);
            return "identity";
        } else {
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
