package com.definesys.comm.ws.email;

import com.definesys.comm.utils.email.EmailUtil;

import com.definesys.comm.ws.object.EmailBean;

import com.definesys.comm.ws.object.Response;

import java.security.GeneralSecurityException;

import javax.jws.WebService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@Path("EmailService")
@WebService(serviceName = "EmailService")
public class EmailService {
    public EmailService() {
        super();
    }


    @Path("sendMail")
    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response sendMail(EmailBean email) {
        
        Response response = new Response();
        try {
            EmailUtil.getInstance().sendSSlMail(email.getFrom(),email.getTo(), email.getCc(),
                                            email.getBcc(), email.getSubject(),
                                            email.getContent(),
                                            email.getEmailSpliter());
        } catch (Exception e) {
            e.printStackTrace();
            response.setBaseMsg(false, "发送邮件失败");
        }
        response.setBaseMsg(true, "发送邮件成功");
        return response;
    }

    /**
     * 转换为json字符串
     * @param json
     * @return
     */
    private String toJson(Object json) {
        String jsonStr = null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        try {
            jsonStr = mapper.writeValueAsString(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonStr;
    }
}
