package com.definesys.comm.ws.util;

import com.definesys.comm.utils.db.DBUtil;

import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.OutputStream;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import java.util.Locale;
import java.util.Map;

import oracle.bpel.services.bpm.common.IBPMContext;
import oracle.bpel.services.workflow.client.IWorkflowServiceClientConstants;
import oracle.bpel.services.workflow.client.WorkflowServiceClientFactory;

import oracle.bpm.client.BPMServiceClientFactory;
import oracle.bpm.collections.Sequence;
import oracle.bpm.draw.diagram.AuditProcessDiagrammer;
import oracle.bpm.draw.diagram.DiagramEvent;
import oracle.bpm.project.model.processes.FlowNode;
import oracle.bpm.project.model.processes.Process;
import oracle.bpm.project.model.processes.SequenceFlow;
import oracle.bpm.services.client.IBPMServiceClient;
import oracle.bpm.services.common.exception.BPMException;
import oracle.bpm.services.instancemanagement.model.IProcessInstance;
import oracle.bpm.services.instancequery.IAuditInstance;
import oracle.bpm.services.instancequery.IInstanceQueryService;
import oracle.bpm.services.internal.processmodel.IProcessModelService;
import oracle.bpm.ui.Image;
import oracle.bpm.ui.utils.ImageExtension;
import oracle.bpm.ui.utils.ImageIOFacade;

public class ProcessImageUtil {

    private static String SOA_URL;
    private static String USER_NAME;
    private static String PASSWORD;

    private IBPMServiceClient bpmServiceClient;
    private IBPMContext bpmContext;
    private BPMServiceClientFactory factory;

    static {
        DBUtil dbUtil = new DBUtil();
        Map<String, String> dbProperties = dbUtil.getAllProperties();
        SOA_URL = dbProperties.get("SOA_URL");//海尔的T3协议（双节点）
//        SOA_URL = "t3://" + dbProperties.get("BPM_SERVER_HOST") + ":" + dbProperties.get("BPM_SERVER_PORT");
        USER_NAME = dbProperties.get("BPM_SECURITY_PRINCIPAL");
        PASSWORD = dbProperties.get("BPM_SECURITY_CREDENTIAL");
    }

    public ProcessImageUtil() {
        this.factory = getBPMServiceClientFactory();
        this.bpmServiceClient = getBPMServiceClient();
        this.bpmContext = getIBPMContextForAuthenticatedUser();
    }

    //    public byte[] archiveDiagramToByte(InputStream istream) throws IOException {
    //        return toByteArray(istream);
    //    }

    public static void main(String[] args) throws Exception {
        //        ProcessImageUtil c = new ProcessImageUtil(getBPMServiceClient());
        //        getIBPMContextForAuthenticatedUser();
        //        try {
        //            InputStream istream= c.getProcessAuditImage(getIBPMContextForAuthenticatedUser(),"3496512");
        //            c.archiveDiagramToFile(istream);
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }
    }

    /**
     * 获取BPM服务客户端
     * @return IBPMServiceClient
     */
    public IBPMServiceClient getBPMServiceClient() {
        if (bpmServiceClient == null) {
            bpmServiceClient = factory.getBPMServiceClient();
        }
        return bpmServiceClient;
    }

    public IBPMContext getIBPMContextForAuthenticatedUser() {
        if (bpmContext == null) {
            try {
                bpmContext = factory.getBPMUserAuthenticationService().getBPMContextForAuthenticatedUser();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bpmContext;

    }

    /**
     * 获取BPM服务工厂
     * @return
     */
    public static BPMServiceClientFactory getBPMServiceClientFactory() {
        Map<IWorkflowServiceClientConstants.CONNECTION_PROPERTY, String> properties =
            new HashMap<IWorkflowServiceClientConstants.CONNECTION_PROPERTY, String>();
        properties.put(IWorkflowServiceClientConstants.CONNECTION_PROPERTY.CLIENT_TYPE, WorkflowServiceClientFactory.REMOTE_CLIENT);
        properties.put(IWorkflowServiceClientConstants.CONNECTION_PROPERTY.EJB_PROVIDER_URL, SOA_URL);
        properties.put(IWorkflowServiceClientConstants.CONNECTION_PROPERTY.EJB_SECURITY_PRINCIPAL, USER_NAME);
        properties.put(IWorkflowServiceClientConstants.CONNECTION_PROPERTY.EJB_SECURITY_CREDENTIALS, PASSWORD);
        return BPMServiceClientFactory.getInstance(properties, null, null);
    }

    /**
     * 获取流程跟踪图方法
     * @param bpmContext 上下文
     * @param instanceId 实例ID
     * @return InputStream 输入流
     */
    public InputStream getProcessAuditImage(IBPMContext bpmContext, String instanceId) throws BPMException {
        IInstanceQueryService instanceQueryService = bpmServiceClient.getInstanceQueryService();
        IProcessInstance processInstance = instanceQueryService.getProcessInstance(bpmContext, instanceId);
        if (processInstance == null) {
            return null;
        }
        IProcessModelService processModelService = bpmServiceClient.getProcessModelService();
        Process process =
            processModelService.getProcessModel(bpmContext, processInstance.getSca().getCompositeDN(), processInstance.getSca().getComponentName()).getProcessModel();
        AuditProcessDiagrammer auditProcessImage = new AuditProcessDiagrammer(process);
        List auditInstances = getBPMServiceClient().getInstanceQueryService().queryAuditInstanceByProcessId(bpmContext, instanceId);

        List diagramEvents = new ArrayList();
        for (int IAuditInstance = 0; IAuditInstance < auditInstances.size(); IAuditInstance++) {
            diagramEvents.addAll(getHighlightEvents(process, (IAuditInstance)auditInstances.get(IAuditInstance)));
        }
        auditProcessImage.highlight(diagramEvents);
        return getProcessImage(auditProcessImage);
    }

    /**
     * 获取流程跟踪图方法
     * @param bpmContext 上下文
     * @param instanceId 实例ID
     * @return byte[]
     * @throws BPMException
     */
    public byte[] getProcessAuditImageByte(IBPMContext bpmContext, String instanceId) throws BPMException {
        IInstanceQueryService instanceQueryService = bpmServiceClient.getInstanceQueryService();
        IProcessInstance processInstance = instanceQueryService.getProcessInstance(bpmContext, instanceId);
        byte[] imageByte = null;
        if (processInstance == null) {
            return imageByte;
        }
        IProcessModelService processModelService = bpmServiceClient.getProcessModelService();
        Process process =
            processModelService.getProcessModel(bpmContext, processInstance.getSca().getCompositeDN(), processInstance.getSca().getComponentName()).getProcessModel();
        AuditProcessDiagrammer auditProcessImage = new AuditProcessDiagrammer(process);
        List auditInstances = getBPMServiceClient().getInstanceQueryService().queryAuditInstanceByProcessId(bpmContext, instanceId);

        List diagramEvents = new ArrayList();
        for (int IAuditInstance = 0; IAuditInstance < auditInstances.size(); IAuditInstance++) {
            diagramEvents.addAll(getHighlightEvents(process, (IAuditInstance)auditInstances.get(IAuditInstance)));
        }
        auditProcessImage.highlight(diagramEvents);
        try {
            imageByte = toByteArray(getProcessImage(auditProcessImage));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageByte;
    }

    /**
     * 获取流程跟踪图调用的子方法，返回流程图的流
     * @param processImage
     * @return InputStream 输入流
     */
    private InputStream getProcessImage(AuditProcessDiagrammer processImage) throws BPMException {
        InputStream processImageStream = null;
        ByteArrayOutputStream auditImageOutputStream = new ByteArrayOutputStream();
        try {
            String base64Image = processImage.getImage();
            Image image = Image.createFromBase64(base64Image);
            BufferedImage bufferedImage = (BufferedImage)image.asAwtImage();
            ImageIOFacade.writeImage(bufferedImage, ImageExtension.PNG, auditImageOutputStream);

            processImageStream = new ByteArrayInputStream(auditImageOutputStream.toByteArray());
        } catch (Exception e) {
            //            logger.severe(e.getMessage());
            throw new BPMException(e);
        } finally {
        }
        return processImageStream;
    }

    /**
     * 调用输出轨迹的方法
     * @param processModel
     * @param auditInstance
     * @retrurn List<DiagramEvent>
     */
    private List<DiagramEvent> getHighlightEvents(Process processModel, IAuditInstance auditInstance) {
        List<DiagramEvent> events = new ArrayList<DiagramEvent>();
        String activityId = auditInstance.getActivityId();
        Date eventDate = auditInstance.getCreateTime().getTime();
        DiagramEvent nodeEvent = DiagramEvent.create(DiagramEvent.DiagramEventType.FLOW_NODE_IN, activityId, eventDate, false);
        events.add(nodeEvent);
        String sourceActivity;
        String targetActivity;
        Iterator<FlowNode> it = processModel.getFlowNodes().iterator();

        if (auditInstance.getAuditInstanceType().equalsIgnoreCase("START")) {
            while (it.hasNext()) {
                FlowNode flowNode = it.next();
                if (flowNode.getId().equals(auditInstance.getActivityId())) {
                    sourceActivity = auditInstance.getSourceActivity();
                    Sequence<SequenceFlow> incommingSequenceFlows = flowNode.getIncomingSequenceFlows();

                    if ((incommingSequenceFlows != null) && (!incommingSequenceFlows.isEmpty()) && (sourceActivity != null)) {
                        Iterator<SequenceFlow> seqIterator = incommingSequenceFlows.iterator();
                        while (seqIterator.hasNext()) {
                            SequenceFlow sequenceFlow = seqIterator.next();
                            if (sequenceFlow.getSource().getId().equalsIgnoreCase(sourceActivity)) {
                                DiagramEvent sequenceEvent =
                                    DiagramEvent.create(DiagramEvent.DiagramEventType.SEQUENCE_FLOW, sequenceFlow.getId(), eventDate, false);
                                events.add(sequenceEvent);
                            }
                        }
                    }
                }
            }
        } else if (auditInstance.getAuditInstanceType().equalsIgnoreCase("END")) {
            while (it.hasNext()) {
                FlowNode flowNode = it.next();
                if (flowNode.getId().equals(auditInstance.getActivityId())) {
                    targetActivity = auditInstance.getTargetActivity();
                    Sequence<SequenceFlow> outgoingSequenceFlows = flowNode.getOutgoingSequenceFlows();
                    if ((outgoingSequenceFlows != null) && (!outgoingSequenceFlows.isEmpty()) && (targetActivity != null)) {
                        Iterator<SequenceFlow> seqIterator = outgoingSequenceFlows.iterator();
                        while (seqIterator.hasNext()) {
                            SequenceFlow sequenceFlow = seqIterator.next();
                            if (sequenceFlow.getTarget().getId().equalsIgnoreCase(targetActivity)) {
                                DiagramEvent sequenceEvent =
                                    DiagramEvent.create(DiagramEvent.DiagramEventType.SEQUENCE_FLOW, sequenceFlow.getId(), eventDate, false);
                                events.add(sequenceEvent);
                            }
                        }
                    }
                }
            }
        }
        return events;
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }
}
