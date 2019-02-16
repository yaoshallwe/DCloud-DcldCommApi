package com.definesys.comm.ws.bpm;

import com.definesys.comm.ws.util.ProcessImageUtil;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;


@Path("DcldProcessImage")
public class DcldProcessImage {

    private static ProcessImageUtil imageUtil = new ProcessImageUtil();

    public DcldProcessImage() {
        super();
    }

    @GET
    @Path("getImage")
    public void getProcessImage(@QueryParam("instanceId") String instanceId, HttpServletResponse response) {
        response.setHeader("Content-Type", "image/png");
        response.setHeader("Cache-Control", "no-cache");

        //获取缩略图
        byte[] respBytes = null;
        try {
            respBytes = imageUtil.getProcessAuditImageByte(imageUtil.getIBPMContextForAuthenticatedUser(), instanceId);
            if (respBytes == null) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            response.setIntHeader("Content-Length", respBytes.length);
            response.getOutputStream().write(respBytes);
            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch (Exception e) {
            e.printStackTrace();
        } 

    }
}
