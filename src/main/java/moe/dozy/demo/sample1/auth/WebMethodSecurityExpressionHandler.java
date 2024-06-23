package moe.dozy.demo.sample1.auth;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

public class WebMethodSecurityExpressionHandler
        extends DefaultMethodSecurityExpressionHandler {

    @Override
	protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
            Authentication authentication, MethodInvocation invocation) {
        WebMethodSecurityExpression expr = new WebMethodSecurityExpression(authentication);
        expr.setPermissionEvaluator(getPermissionEvaluator());
        expr.setTrustResolver(getTrustResolver());
        expr.setRoleHierarchy(getRoleHierarchy());
        return expr;
    }
}
