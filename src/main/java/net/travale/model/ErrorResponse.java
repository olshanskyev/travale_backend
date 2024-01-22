package net.travale.model;

import java.util.Map;

public class ErrorResponse {
    private String errorText;
    private Map<String, String> parameters; //parameter name, parameter value

    public String getErrorText() {
        return errorText;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public ErrorResponse(String errorText) {
        this.errorText = errorText;
    }

    public ErrorResponse(String errorText, Map<String, String> parameters){
        this(errorText);
        this.parameters = parameters;
    }

}
