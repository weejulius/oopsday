package com.fishstory.mvc;

/**
 * User: Julius.Yu
 * Date: 12/6/11
 */
public class RequestDelegation {
    private String contextPath;
    private String packageOfEntries;

    public RequestDelegation(String contextPath, String packageOfEntries) {
        this.contextPath = contextPath;
        this.packageOfEntries = packageOfEntries;
    }

    public String delegateTo(String request) {
        String publicEntryName = cutOffPublicEntryName(request);
        return buildEntryFullClassName(publicEntryName);
    }

    private String buildEntryFullClassName(String publicEntryName) {
        return packageOfEntries+"."+publicEntryName.substring(0,1).toUpperCase()+publicEntryName.substring(1)+"Entry";
    }

    private String cutOffPublicEntryName(String request) {
        int endOfProtocol = request.indexOf("//");
        int beginOfPublicEntryName = request.indexOf(contextPath,endOfProtocol<0?0:endOfProtocol+2)+1;
        int endOfPublicEntryName = request.indexOf("/",beginOfPublicEntryName);
        return request.substring(beginOfPublicEntryName,endOfPublicEntryName);
    }
}
