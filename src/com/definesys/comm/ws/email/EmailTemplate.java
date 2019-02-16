package com.definesys.comm.ws.email;

import com.definesys.comm.utils.db.DBUtil;
import com.definesys.comm.utils.email.EmailUtil;
import com.definesys.comm.utils.template.FreeMarkerTemplateUtil;
import com.definesys.comm.ws.object.EmailBean;
import com.definesys.comm.ws.object.Response;
import com.definesys.comm.ws.object.template.BusinessParams;
import com.definesys.comm.ws.object.template.EmailTemplateData;

import com.definesys.comm.ws.object.template.OthersParams;
import com.definesys.comm.ws.object.template.ParamsItem;
import com.definesys.comm.ws.object.template.ProcessParams;

import java.security.GeneralSecurityException;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.Map;


import javax.jws.WebService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import oracle.jdbc.OracleTypes;

import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;

@Path("EmailTemplateService")
@WebService(serviceName = "EmailTemplateService")
public class EmailTemplate {
    public EmailTemplate() {
        super();
    }

    public final String EMAIL_TITLE_KEY = "EMAIL_TITLE_KEY";
    public final String EMAIL_BODY_KEY = "EMAIL_BODY_KEY";
    public final String RESULT_FLAG = "RESULT_FLAG";
    public final String ERROR_MSG = "ERROR_MSG";

    @Path("sendEmailByTemplate")
    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response sendEmailByTemplate(EmailTemplateData emailTemplateData) {
        Response response = new Response();
        Response getContentRsp = getEmailContent(emailTemplateData);
        EmailBean email = null;
        if (Response.SUCCESS.equals(getContentRsp.getCode())){
            email = (EmailBean)getContentRsp.getData();
        }else {
            //获取模板失败
            return getContentRsp;
        }
        EmailBean params = emailTemplateData.getEmailInfo();
        email.setFrom(params.getFrom());
        email.setCc(params.getCc());
        email.setBcc(params.getBcc());
        email.setEmailSpliter(params.getEmailSpliter());
        email.setTo(params.getTo());
        try {
            EmailUtil.getInstance().sendSSlMail(email.getFrom(), email.getTo(),
                                                email.getCc(), email.getBcc(),
                                                email.getSubject(),
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
     * 获取邮件的主题和内容
     * @param emailTemplateData
     * @return
     */
    @Path("getEmailContent")
    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public Response getEmailContent(EmailTemplateData emailTemplateData) {
        EmailBean eb = new EmailBean();
        Response response = new Response();
        //获取模板
        Map<String, String> emailTemplateMap =
            getTemplate(emailTemplateData.getProcessParams(),
                        emailTemplateData.getInstanceId(),
                        emailTemplateData.getTaskId(),
                        emailTemplateData.getOutcome(),
                        emailTemplateData.getLocale());
        if ("N".equals(emailTemplateMap.get(RESULT_FLAG))) {
            //TODO
            response.setBaseMsg(false, emailTemplateMap.get(ERROR_MSG));
//            eb.setSubject(emailTemplateMap.get(ERROR_MSG));
//            eb.setContent(emailTemplateMap.get(ERROR_MSG));
            return response;
        }
        //获取数据集
        Map<String, String> map =
            getDataSet(emailTemplateData.getBusinessParams(),
                       emailTemplateData.getProcessParams(),
                       emailTemplateData.getOthersParams(),
                       emailTemplateData.getInstanceId(),
                       emailTemplateData.getOutcome(),
                       emailTemplateData.getTaskId(),
                       emailTemplateData.getLocale());
        //获取邮件内容模板
        String content = emailTemplateMap.get(EMAIL_BODY_KEY);
        //获取邮件主题模板
        String subject = emailTemplateMap.get(EMAIL_TITLE_KEY);
        //解析邮件内容
        content = FreeMarkerTemplateUtil.templateParser(content, map);
        //解析邮件主题
        subject = FreeMarkerTemplateUtil.templateParser(subject, map);

        eb.setSubject(subject);
        eb.setContent(content);
        
        response.setBaseMsg(true, "");
        response.setData(eb);
        return response;
    }

    /**
     * 获取邮件模板
     * @param pp 流程参数
     * @param instancId 流程ID
     * @param taskId 待办ID
     * @param outcome task任务的结果 
     * @param locale 国际化
     * @return
     */
    private Map<String, String> getTemplate(ProcessParams pp,
                                            String instancId, String taskId,
                                            String outcome,String locale) {
        Map<String, String> map = new HashMap<String, String>();
        DBUtil dbUtil = new DBUtil();
        Connection conn = dbUtil.getJNDIConnection(DBUtil.DATASOURCE);
        CallableStatement cstmt = null;
        ResultSet rs = null;
        if (pp == null) {
            pp = new ProcessParams();
        }

        try {
            cstmt =
                    conn.prepareCall("{call DBPM_EMAIL_API_PKG.proc_get_email_template(?,?,?,?,?,?,?,?,?)}");
            cstmt.setString(1, instancId);
            cstmt.setString(2, taskId);
            cstmt.setString(3, outcome);
            cstmt.setString(4, locale);
            //            CREATE OR REPLACE TYPE "DBPM_PROCESS_PARAM_REC"                                                                          IS OBJECT
            //            (
            //              processFormNo   VARCHAR2(2000),
            //              processApplier  VARCHAR2(2000),
            //              processParam    VARCHAR2(2000),
            //              processKeyword  VARCHAR2(2000),
            //              processTitle    VARCHAR2(4000),
            //              processCode     VARCHAR2(2000),
            //              businessSysCode VARCHAR2(2000)
            //            )
            StructDescriptor recDesc =
                StructDescriptor.createDescriptor("DBPM_PROCESS_PARAM_REC",
                                                  conn); //对应的数据库type名称，一定要大写

            Object[] record = new Object[7];
            record[0] = pp.getProcessFormNo();
            record[1] = pp.getProcessApplier();
            record[2] = pp.getProcessParam();
            record[3] = pp.getProcessKeyword();
            record[4] = pp.getProcessTitle();
            record[5] = pp.getProcessCode();
            record[6] = pp.getBusinessSysCode();
            STRUCT structProcessParams = new STRUCT(recDesc, conn, record);
            cstmt.setObject(5, structProcessParams, OracleTypes.STRUCT);
            cstmt.registerOutParameter(6,
                                       java.sql.Types.VARCHAR); //x_email_title
            cstmt.registerOutParameter(7,
                                       java.sql.Types.VARCHAR); //x_email_body
            cstmt.registerOutParameter(8,
                                       java.sql.Types.VARCHAR); //x_result_flag
            cstmt.registerOutParameter(9,
                                       java.sql.Types.VARCHAR); //x_error_msg
            cstmt.execute();

            String subject = cstmt.getString(6);
            String emailBody = cstmt.getString(7);
            String resultFlag = cstmt.getString(8);
            String errorMsg = cstmt.getString(9);

            map.put(EMAIL_TITLE_KEY, subject);
            map.put(EMAIL_BODY_KEY, emailBody);
            map.put(RESULT_FLAG, resultFlag);
            map.put(ERROR_MSG, errorMsg);

        } catch (SQLException e) {
            // TODOAuto-generated catch block
            e.printStackTrace();
        } finally {
            dbUtil.close(conn, cstmt, rs);
        }

        return map;
    }

    /**
     * 获取数据集合
     * @param bp 业务参数
     * @param pp 流程参数
     * @param op 扩展参数
     * @param instanceId 单据ID
     * @param outcome task任务的结果
     * @param taskId 待办ID
     * @param locale 国际化
     * @return
     */
    private Map<String, String> getDataSet(BusinessParams bp, ProcessParams pp,
                                           OthersParams op, String instanceId,
                                           String outcome, String taskId,String locale) {
        DBUtil dbUtil = new DBUtil();
        Connection conn = dbUtil.getJNDIConnection(DBUtil.DATASOURCE);
        CallableStatement cstmt = null;
        ResultSet rs = null;
        Map<String, String> map = new HashMap<String, String>();
        //初始化map
        //根据BusinessParams初始化
        map = initBusinessParamsMap(map, bp);
        //根据ProcessParams初始化
        map = initProcessParamsMap(map, pp);
        //根据OthersParams初始化
        map = initOthersParamsMap(map, op);
        try {
            cstmt =
                    conn.prepareCall("{call DBPM_EMAIL_API_PKG.proc_get_data_set(?,?,?,?,?,?,?,?)}");
            cstmt.setString(1, instanceId);
            cstmt.setString(2, taskId);
            cstmt.setString(3, outcome);
            cstmt.setString(4, locale);
            StructDescriptor recDesc =
                StructDescriptor.createDescriptor("DBPM_PROCESS_PARAM_REC",
                                                  conn);
            Object[] record = new Object[7];
            record[0] = pp.getProcessFormNo();
            record[1] = pp.getProcessApplier();
            record[2] = pp.getProcessParam();
            record[3] = pp.getProcessKeyword();
            record[4] = pp.getProcessTitle();
            record[5] = pp.getProcessCode();
            record[6] = pp.getBusinessSysCode();
            STRUCT structProcessParams = new STRUCT(recDesc, conn, record);
            cstmt.setObject(5, structProcessParams, OracleTypes.STRUCT);
            cstmt.setString(6, ""); //sql
            cstmt.registerOutParameter(7, OracleTypes.CURSOR); //out类型需要注册
            cstmt.registerOutParameter(8, OracleTypes.CURSOR); //out类型需要注册
            cstmt.execute();

            //处理单行的cursor
            rs = (ResultSet)cstmt.getObject(7); 
            ResultSetMetaData rsm = rs.getMetaData();
            int columnCount = rsm.getColumnCount();
            Object obj = null;
            String objStr = "";
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    obj = rs.getObject(i);
                    objStr = obj == null ? null : obj.toString();
                    map.put(rsm.getColumnName(i), objStr);
                }
            }
            
            //处理多行的cursor
            rs.close();//关闭上一个cursor
            rs = (ResultSet)cstmt.getObject(8); //此处的2要与存储过程中cursor的问题对应
            while (rs.next()) {
                map.put(rs.getString(1), rs.getString(2));
            }

        } catch (SQLException e) {
            // TODOAuto-generated catch block
            e.printStackTrace();
        } finally {
            dbUtil.close(conn, cstmt, rs);
        }

        return map;
    }

    /**
     * 根据业务参数初始化map
     * @param map
     * @param bp
     * @return
     */
    private Map<String, String> initBusinessParamsMap(Map<String, String> map,
                                                      BusinessParams bp) {
        if (bp == null)
            return map;
        for (ParamsItem pi : bp.getParamsList()) {
            map.put(pi.getKey(), pi.getValue());
        }
        return map;
    }

    /**
     * 根据流程参数初始化map
     * @param map
     * @param pp
     * @return
     */
    private Map<String, String> initProcessParamsMap(Map<String, String> map,
                                                     ProcessParams pp) {
        map.put("processCode", pp.getProcessCode());
        map.put("processFormNo", pp.getProcessFormNo());
        map.put("processApplier", pp.getProcessApplier());
        map.put("processParam", pp.getProcessParam());
        map.put("processKeyword", pp.getProcessKeyword());
        map.put("processTitle", pp.getProcessTitle());
        map.put("businessSysCode", pp.getBusinessSysCode());
        return map;
    }

    /**
     * 根据其他参数初始化map
     * @param map
     * @param op
     * @return
     */
    private Map<String, String> initOthersParamsMap(Map<String, String> map,
                                                    OthersParams op) {
        if (op == null) {
            return map;
        }
        for (ParamsItem pi : op.getParamsList()) {
            map.put(pi.getKey(), pi.getValue());
        }
        return map;
    }

    //    private void printRequest(EmailTemplateData emailTemplateData){
    //
    //        System.out.println(emailTemplateData.getDocumentId());
    //        System.out.println(emailTemplateData.getTaskId());
    //        System.out.println(emailTemplateData.getEmailInfo().getSubject());
    //        System.out.println(emailTemplateData.getBusinessParams().getParamsList().get(0).getKey());
    //        System.out.println(emailTemplateData.getBusinessParams().getParamsList().get(0).getValue());
    //    }
}
