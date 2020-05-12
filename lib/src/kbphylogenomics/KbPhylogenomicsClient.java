package kbphylogenomics;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import us.kbase.auth.AuthToken;
import us.kbase.common.service.JobState;
import us.kbase.common.service.JsonClientCaller;
import us.kbase.common.service.JsonClientException;
import us.kbase.common.service.RpcContext;
import us.kbase.common.service.UnauthorizedException;

/**
 * <p>Original spec-file module name: kb_phylogenomics</p>
 * <pre>
 * A KBase module: kb_phylogenomics
 * This module contains methods for running and visualizing results of phylogenomics and comparative genomics analyses
 * </pre>
 */
public class KbPhylogenomicsClient {
    private JsonClientCaller caller;
    private long asyncJobCheckTimeMs = 100;
    private int asyncJobCheckTimeScalePercent = 150;
    private long asyncJobCheckMaxTimeMs = 300000;  // 5 minutes
    private String serviceVersion = "release";


    /** Constructs a client with a custom URL and no user credentials.
     * @param url the URL of the service.
     */
    public KbPhylogenomicsClient(URL url) {
        caller = new JsonClientCaller(url);
    }
    /** Constructs a client with a custom URL.
     * @param url the URL of the service.
     * @param token the user's authorization token.
     * @throws UnauthorizedException if the token is not valid.
     * @throws IOException if an IOException occurs when checking the token's
     * validity.
     */
    public KbPhylogenomicsClient(URL url, AuthToken token) throws UnauthorizedException, IOException {
        caller = new JsonClientCaller(url, token);
    }

    /** Constructs a client with a custom URL.
     * @param url the URL of the service.
     * @param user the user name.
     * @param password the password for the user name.
     * @throws UnauthorizedException if the credentials are not valid.
     * @throws IOException if an IOException occurs when checking the user's
     * credentials.
     */
    public KbPhylogenomicsClient(URL url, String user, String password) throws UnauthorizedException, IOException {
        caller = new JsonClientCaller(url, user, password);
    }

    /** Constructs a client with a custom URL
     * and a custom authorization service URL.
     * @param url the URL of the service.
     * @param user the user name.
     * @param password the password for the user name.
     * @param auth the URL of the authorization server.
     * @throws UnauthorizedException if the credentials are not valid.
     * @throws IOException if an IOException occurs when checking the user's
     * credentials.
     */
    public KbPhylogenomicsClient(URL url, String user, String password, URL auth) throws UnauthorizedException, IOException {
        caller = new JsonClientCaller(url, user, password, auth);
    }

    /** Get the token this client uses to communicate with the server.
     * @return the authorization token.
     */
    public AuthToken getToken() {
        return caller.getToken();
    }

    /** Get the URL of the service with which this client communicates.
     * @return the service URL.
     */
    public URL getURL() {
        return caller.getURL();
    }

    /** Set the timeout between establishing a connection to a server and
     * receiving a response. A value of zero or null implies no timeout.
     * @param milliseconds the milliseconds to wait before timing out when
     * attempting to read from a server.
     */
    public void setConnectionReadTimeOut(Integer milliseconds) {
        this.caller.setConnectionReadTimeOut(milliseconds);
    }

    /** Check if this client allows insecure http (vs https) connections.
     * @return true if insecure connections are allowed.
     */
    public boolean isInsecureHttpConnectionAllowed() {
        return caller.isInsecureHttpConnectionAllowed();
    }

    /** Deprecated. Use isInsecureHttpConnectionAllowed().
     * @deprecated
     */
    public boolean isAuthAllowedForHttp() {
        return caller.isAuthAllowedForHttp();
    }

    /** Set whether insecure http (vs https) connections should be allowed by
     * this client.
     * @param allowed true to allow insecure connections. Default false
     */
    public void setIsInsecureHttpConnectionAllowed(boolean allowed) {
        caller.setInsecureHttpConnectionAllowed(allowed);
    }

    /** Deprecated. Use setIsInsecureHttpConnectionAllowed().
     * @deprecated
     */
    public void setAuthAllowedForHttp(boolean isAuthAllowedForHttp) {
        caller.setAuthAllowedForHttp(isAuthAllowedForHttp);
    }

    /** Set whether all SSL certificates, including self-signed certificates,
     * should be trusted.
     * @param trustAll true to trust all certificates. Default false.
     */
    public void setAllSSLCertificatesTrusted(final boolean trustAll) {
        caller.setAllSSLCertificatesTrusted(trustAll);
    }
    
    /** Check if this client trusts all SSL certificates, including
     * self-signed certificates.
     * @return true if all certificates are trusted.
     */
    public boolean isAllSSLCertificatesTrusted() {
        return caller.isAllSSLCertificatesTrusted();
    }
    /** Sets streaming mode on. In this case, the data will be streamed to
     * the server in chunks as it is read from disk rather than buffered in
     * memory. Many servers are not compatible with this feature.
     * @param streamRequest true to set streaming mode on, false otherwise.
     */
    public void setStreamingModeOn(boolean streamRequest) {
        caller.setStreamingModeOn(streamRequest);
    }

    /** Returns true if streaming mode is on.
     * @return true if streaming mode is on.
     */
    public boolean isStreamingModeOn() {
        return caller.isStreamingModeOn();
    }

    public void _setFileForNextRpcResponse(File f) {
        caller.setFileForNextRpcResponse(f);
    }

    public long getAsyncJobCheckTimeMs() {
        return this.asyncJobCheckTimeMs;
    }

    public void setAsyncJobCheckTimeMs(long newValue) {
        this.asyncJobCheckTimeMs = newValue;
    }

    public int getAsyncJobCheckTimeScalePercent() {
        return this.asyncJobCheckTimeScalePercent;
    }

    public void setAsyncJobCheckTimeScalePercent(int newValue) {
        this.asyncJobCheckTimeScalePercent = newValue;
    }

    public long getAsyncJobCheckMaxTimeMs() {
        return this.asyncJobCheckMaxTimeMs;
    }

    public void setAsyncJobCheckMaxTimeMs(long newValue) {
        this.asyncJobCheckMaxTimeMs = newValue;
    }

    public String getServiceVersion() {
        return this.serviceVersion;
    }

    public void setServiceVersion(String newValue) {
        this.serviceVersion = newValue;
    }

    protected <T> JobState<T> _checkJob(String jobId, TypeReference<List<JobState<T>>> retType) throws IOException, JsonClientException {
        List<Object> args = new ArrayList<Object>();
        args.add(jobId);
        List<JobState<T>> res = caller.jsonrpcCall("kb_phylogenomics._check_job", args, retType, true, true);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: view_tree</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link kbphylogenomics.ViewTreeInput ViewTreeInput} (original type "view_tree_Input")
     * @return   parameter "output" of type {@link kbphylogenomics.ViewTreeOutput ViewTreeOutput} (original type "view_tree_Output")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    protected String _viewTreeSubmit(ViewTreeInput params, RpcContext... jsonRpcContext) throws IOException, JsonClientException {
        if (this.serviceVersion != null) {
            if (jsonRpcContext == null || jsonRpcContext.length == 0 || jsonRpcContext[0] == null)
                jsonRpcContext = new RpcContext[] {new RpcContext()};
            jsonRpcContext[0].getAdditionalProperties().put("service_ver", this.serviceVersion);
        }
        List<Object> args = new ArrayList<Object>();
        args.add(params);
        TypeReference<List<String>> retType = new TypeReference<List<String>>() {};
        List<String> res = caller.jsonrpcCall("kb_phylogenomics._view_tree_submit", args, retType, true, true, jsonRpcContext);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: view_tree</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link kbphylogenomics.ViewTreeInput ViewTreeInput} (original type "view_tree_Input")
     * @return   parameter "output" of type {@link kbphylogenomics.ViewTreeOutput ViewTreeOutput} (original type "view_tree_Output")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public ViewTreeOutput viewTree(ViewTreeInput params, RpcContext... jsonRpcContext) throws IOException, JsonClientException {
        String jobId = _viewTreeSubmit(params, jsonRpcContext);
        TypeReference<List<JobState<List<ViewTreeOutput>>>> retType = new TypeReference<List<JobState<List<ViewTreeOutput>>>>() {};
        long asyncJobCheckTimeMs = this.asyncJobCheckTimeMs;
        while (true) {
            if (Thread.currentThread().isInterrupted())
                throw new JsonClientException("Thread was interrupted");
            try { 
                Thread.sleep(asyncJobCheckTimeMs);
            } catch(Exception ex) {
                throw new JsonClientException("Thread was interrupted", ex);
            }
            asyncJobCheckTimeMs = Math.min(asyncJobCheckTimeMs * this.asyncJobCheckTimeScalePercent / 100, this.asyncJobCheckMaxTimeMs);
            JobState<List<ViewTreeOutput>> res = _checkJob(jobId, retType);
            if (res.getFinished() != 0L)
                return res.getResult().get(0);
        }
    }

    /**
     * <p>Original spec-file function name: trim_speciestree_to_genomeset</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link kbphylogenomics.TrimSpeciestreeToGenomesetInput TrimSpeciestreeToGenomesetInput} (original type "trim_speciestree_to_genomeset_Input")
     * @return   parameter "output" of type {@link kbphylogenomics.TrimSpeciestreeToGenomesetOutput TrimSpeciestreeToGenomesetOutput} (original type "trim_speciestree_to_genomeset_Output")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    protected String _trimSpeciestreeToGenomesetSubmit(TrimSpeciestreeToGenomesetInput params, RpcContext... jsonRpcContext) throws IOException, JsonClientException {
        if (this.serviceVersion != null) {
            if (jsonRpcContext == null || jsonRpcContext.length == 0 || jsonRpcContext[0] == null)
                jsonRpcContext = new RpcContext[] {new RpcContext()};
            jsonRpcContext[0].getAdditionalProperties().put("service_ver", this.serviceVersion);
        }
        List<Object> args = new ArrayList<Object>();
        args.add(params);
        TypeReference<List<String>> retType = new TypeReference<List<String>>() {};
        List<String> res = caller.jsonrpcCall("kb_phylogenomics._trim_speciestree_to_genomeset_submit", args, retType, true, true, jsonRpcContext);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: trim_speciestree_to_genomeset</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link kbphylogenomics.TrimSpeciestreeToGenomesetInput TrimSpeciestreeToGenomesetInput} (original type "trim_speciestree_to_genomeset_Input")
     * @return   parameter "output" of type {@link kbphylogenomics.TrimSpeciestreeToGenomesetOutput TrimSpeciestreeToGenomesetOutput} (original type "trim_speciestree_to_genomeset_Output")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public TrimSpeciestreeToGenomesetOutput trimSpeciestreeToGenomeset(TrimSpeciestreeToGenomesetInput params, RpcContext... jsonRpcContext) throws IOException, JsonClientException {
        String jobId = _trimSpeciestreeToGenomesetSubmit(params, jsonRpcContext);
        TypeReference<List<JobState<List<TrimSpeciestreeToGenomesetOutput>>>> retType = new TypeReference<List<JobState<List<TrimSpeciestreeToGenomesetOutput>>>>() {};
        long asyncJobCheckTimeMs = this.asyncJobCheckTimeMs;
        while (true) {
            if (Thread.currentThread().isInterrupted())
                throw new JsonClientException("Thread was interrupted");
            try { 
                Thread.sleep(asyncJobCheckTimeMs);
            } catch(Exception ex) {
                throw new JsonClientException("Thread was interrupted", ex);
            }
            asyncJobCheckTimeMs = Math.min(asyncJobCheckTimeMs * this.asyncJobCheckTimeScalePercent / 100, this.asyncJobCheckMaxTimeMs);
            JobState<List<TrimSpeciestreeToGenomesetOutput>> res = _checkJob(jobId, retType);
            if (res.getFinished() != 0L)
                return res.getResult().get(0);
        }
    }

    /**
     * <p>Original spec-file function name: run_DomainAnnotation_Sets</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link kbphylogenomics.RunDomainAnnotationSetsInput RunDomainAnnotationSetsInput} (original type "run_DomainAnnotation_Sets_Input")
     * @return   parameter "output" of type {@link kbphylogenomics.RunDomainAnnotationSetsOutput RunDomainAnnotationSetsOutput} (original type "run_DomainAnnotation_Sets_Output")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    protected String _runDomainAnnotationSetsSubmit(RunDomainAnnotationSetsInput params, RpcContext... jsonRpcContext) throws IOException, JsonClientException {
        if (this.serviceVersion != null) {
            if (jsonRpcContext == null || jsonRpcContext.length == 0 || jsonRpcContext[0] == null)
                jsonRpcContext = new RpcContext[] {new RpcContext()};
            jsonRpcContext[0].getAdditionalProperties().put("service_ver", this.serviceVersion);
        }
        List<Object> args = new ArrayList<Object>();
        args.add(params);
        TypeReference<List<String>> retType = new TypeReference<List<String>>() {};
        List<String> res = caller.jsonrpcCall("kb_phylogenomics._run_DomainAnnotation_Sets_submit", args, retType, true, true, jsonRpcContext);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: run_DomainAnnotation_Sets</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link kbphylogenomics.RunDomainAnnotationSetsInput RunDomainAnnotationSetsInput} (original type "run_DomainAnnotation_Sets_Input")
     * @return   parameter "output" of type {@link kbphylogenomics.RunDomainAnnotationSetsOutput RunDomainAnnotationSetsOutput} (original type "run_DomainAnnotation_Sets_Output")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public RunDomainAnnotationSetsOutput runDomainAnnotationSets(RunDomainAnnotationSetsInput params, RpcContext... jsonRpcContext) throws IOException, JsonClientException {
        String jobId = _runDomainAnnotationSetsSubmit(params, jsonRpcContext);
        TypeReference<List<JobState<List<RunDomainAnnotationSetsOutput>>>> retType = new TypeReference<List<JobState<List<RunDomainAnnotationSetsOutput>>>>() {};
        long asyncJobCheckTimeMs = this.asyncJobCheckTimeMs;
        while (true) {
            if (Thread.currentThread().isInterrupted())
                throw new JsonClientException("Thread was interrupted");
            try { 
                Thread.sleep(asyncJobCheckTimeMs);
            } catch(Exception ex) {
                throw new JsonClientException("Thread was interrupted", ex);
            }
            asyncJobCheckTimeMs = Math.min(asyncJobCheckTimeMs * this.asyncJobCheckTimeScalePercent / 100, this.asyncJobCheckMaxTimeMs);
            JobState<List<RunDomainAnnotationSetsOutput>> res = _checkJob(jobId, retType);
            if (res.getFinished() != 0L)
                return res.getResult().get(0);
        }
    }

    /**
     * <p>Original spec-file function name: view_fxn_profile</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link kbphylogenomics.ViewFxnProfileInput ViewFxnProfileInput} (original type "view_fxn_profile_Input")
     * @return   parameter "output" of type {@link kbphylogenomics.ViewFxnProfileOutput ViewFxnProfileOutput} (original type "view_fxn_profile_Output")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    protected String _viewFxnProfileSubmit(ViewFxnProfileInput params, RpcContext... jsonRpcContext) throws IOException, JsonClientException {
        if (this.serviceVersion != null) {
            if (jsonRpcContext == null || jsonRpcContext.length == 0 || jsonRpcContext[0] == null)
                jsonRpcContext = new RpcContext[] {new RpcContext()};
            jsonRpcContext[0].getAdditionalProperties().put("service_ver", this.serviceVersion);
        }
        List<Object> args = new ArrayList<Object>();
        args.add(params);
        TypeReference<List<String>> retType = new TypeReference<List<String>>() {};
        List<String> res = caller.jsonrpcCall("kb_phylogenomics._view_fxn_profile_submit", args, retType, true, true, jsonRpcContext);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: view_fxn_profile</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link kbphylogenomics.ViewFxnProfileInput ViewFxnProfileInput} (original type "view_fxn_profile_Input")
     * @return   parameter "output" of type {@link kbphylogenomics.ViewFxnProfileOutput ViewFxnProfileOutput} (original type "view_fxn_profile_Output")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public ViewFxnProfileOutput viewFxnProfile(ViewFxnProfileInput params, RpcContext... jsonRpcContext) throws IOException, JsonClientException {
        String jobId = _viewFxnProfileSubmit(params, jsonRpcContext);
        TypeReference<List<JobState<List<ViewFxnProfileOutput>>>> retType = new TypeReference<List<JobState<List<ViewFxnProfileOutput>>>>() {};
        long asyncJobCheckTimeMs = this.asyncJobCheckTimeMs;
        while (true) {
            if (Thread.currentThread().isInterrupted())
                throw new JsonClientException("Thread was interrupted");
            try { 
                Thread.sleep(asyncJobCheckTimeMs);
            } catch(Exception ex) {
                throw new JsonClientException("Thread was interrupted", ex);
            }
            asyncJobCheckTimeMs = Math.min(asyncJobCheckTimeMs * this.asyncJobCheckTimeScalePercent / 100, this.asyncJobCheckMaxTimeMs);
            JobState<List<ViewFxnProfileOutput>> res = _checkJob(jobId, retType);
            if (res.getFinished() != 0L)
                return res.getResult().get(0);
        }
    }

    /**
     * <p>Original spec-file function name: view_fxn_profile_featureSet</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link kbphylogenomics.ViewFxnProfileFeatureSetInput ViewFxnProfileFeatureSetInput} (original type "view_fxn_profile_featureSet_Input")
     * @return   parameter "output" of type {@link kbphylogenomics.ViewFxnProfileFeatureSetOutput ViewFxnProfileFeatureSetOutput} (original type "view_fxn_profile_featureSet_Output")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    protected String _viewFxnProfileFeatureSetSubmit(ViewFxnProfileFeatureSetInput params, RpcContext... jsonRpcContext) throws IOException, JsonClientException {
        if (this.serviceVersion != null) {
            if (jsonRpcContext == null || jsonRpcContext.length == 0 || jsonRpcContext[0] == null)
                jsonRpcContext = new RpcContext[] {new RpcContext()};
            jsonRpcContext[0].getAdditionalProperties().put("service_ver", this.serviceVersion);
        }
        List<Object> args = new ArrayList<Object>();
        args.add(params);
        TypeReference<List<String>> retType = new TypeReference<List<String>>() {};
        List<String> res = caller.jsonrpcCall("kb_phylogenomics._view_fxn_profile_featureSet_submit", args, retType, true, true, jsonRpcContext);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: view_fxn_profile_featureSet</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link kbphylogenomics.ViewFxnProfileFeatureSetInput ViewFxnProfileFeatureSetInput} (original type "view_fxn_profile_featureSet_Input")
     * @return   parameter "output" of type {@link kbphylogenomics.ViewFxnProfileFeatureSetOutput ViewFxnProfileFeatureSetOutput} (original type "view_fxn_profile_featureSet_Output")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public ViewFxnProfileFeatureSetOutput viewFxnProfileFeatureSet(ViewFxnProfileFeatureSetInput params, RpcContext... jsonRpcContext) throws IOException, JsonClientException {
        String jobId = _viewFxnProfileFeatureSetSubmit(params, jsonRpcContext);
        TypeReference<List<JobState<List<ViewFxnProfileFeatureSetOutput>>>> retType = new TypeReference<List<JobState<List<ViewFxnProfileFeatureSetOutput>>>>() {};
        long asyncJobCheckTimeMs = this.asyncJobCheckTimeMs;
        while (true) {
            if (Thread.currentThread().isInterrupted())
                throw new JsonClientException("Thread was interrupted");
            try { 
                Thread.sleep(asyncJobCheckTimeMs);
            } catch(Exception ex) {
                throw new JsonClientException("Thread was interrupted", ex);
            }
            asyncJobCheckTimeMs = Math.min(asyncJobCheckTimeMs * this.asyncJobCheckTimeScalePercent / 100, this.asyncJobCheckMaxTimeMs);
            JobState<List<ViewFxnProfileFeatureSetOutput>> res = _checkJob(jobId, retType);
            if (res.getFinished() != 0L)
                return res.getResult().get(0);
        }
    }

    /**
     * <p>Original spec-file function name: view_fxn_profile_phylo</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link kbphylogenomics.ViewFxnProfilePhyloInput ViewFxnProfilePhyloInput} (original type "view_fxn_profile_phylo_Input")
     * @return   parameter "output" of type {@link kbphylogenomics.ViewFxnProfilePhyloOutput ViewFxnProfilePhyloOutput} (original type "view_fxn_profile_phylo_Output")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    protected String _viewFxnProfilePhyloSubmit(ViewFxnProfilePhyloInput params, RpcContext... jsonRpcContext) throws IOException, JsonClientException {
        if (this.serviceVersion != null) {
            if (jsonRpcContext == null || jsonRpcContext.length == 0 || jsonRpcContext[0] == null)
                jsonRpcContext = new RpcContext[] {new RpcContext()};
            jsonRpcContext[0].getAdditionalProperties().put("service_ver", this.serviceVersion);
        }
        List<Object> args = new ArrayList<Object>();
        args.add(params);
        TypeReference<List<String>> retType = new TypeReference<List<String>>() {};
        List<String> res = caller.jsonrpcCall("kb_phylogenomics._view_fxn_profile_phylo_submit", args, retType, true, true, jsonRpcContext);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: view_fxn_profile_phylo</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link kbphylogenomics.ViewFxnProfilePhyloInput ViewFxnProfilePhyloInput} (original type "view_fxn_profile_phylo_Input")
     * @return   parameter "output" of type {@link kbphylogenomics.ViewFxnProfilePhyloOutput ViewFxnProfilePhyloOutput} (original type "view_fxn_profile_phylo_Output")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public ViewFxnProfilePhyloOutput viewFxnProfilePhylo(ViewFxnProfilePhyloInput params, RpcContext... jsonRpcContext) throws IOException, JsonClientException {
        String jobId = _viewFxnProfilePhyloSubmit(params, jsonRpcContext);
        TypeReference<List<JobState<List<ViewFxnProfilePhyloOutput>>>> retType = new TypeReference<List<JobState<List<ViewFxnProfilePhyloOutput>>>>() {};
        long asyncJobCheckTimeMs = this.asyncJobCheckTimeMs;
        while (true) {
            if (Thread.currentThread().isInterrupted())
                throw new JsonClientException("Thread was interrupted");
            try { 
                Thread.sleep(asyncJobCheckTimeMs);
            } catch(Exception ex) {
                throw new JsonClientException("Thread was interrupted", ex);
            }
            asyncJobCheckTimeMs = Math.min(asyncJobCheckTimeMs * this.asyncJobCheckTimeScalePercent / 100, this.asyncJobCheckMaxTimeMs);
            JobState<List<ViewFxnProfilePhyloOutput>> res = _checkJob(jobId, retType);
            if (res.getFinished() != 0L)
                return res.getResult().get(0);
        }
    }

    /**
     * <p>Original spec-file function name: view_genome_circle_plot</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link kbphylogenomics.ViewGenomeCirclePlotInput ViewGenomeCirclePlotInput} (original type "view_genome_circle_plot_Input")
     * @return   parameter "output" of type {@link kbphylogenomics.ViewGenomeCirclePlotOutput ViewGenomeCirclePlotOutput} (original type "view_genome_circle_plot_Output")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    protected String _viewGenomeCirclePlotSubmit(ViewGenomeCirclePlotInput params, RpcContext... jsonRpcContext) throws IOException, JsonClientException {
        if (this.serviceVersion != null) {
            if (jsonRpcContext == null || jsonRpcContext.length == 0 || jsonRpcContext[0] == null)
                jsonRpcContext = new RpcContext[] {new RpcContext()};
            jsonRpcContext[0].getAdditionalProperties().put("service_ver", this.serviceVersion);
        }
        List<Object> args = new ArrayList<Object>();
        args.add(params);
        TypeReference<List<String>> retType = new TypeReference<List<String>>() {};
        List<String> res = caller.jsonrpcCall("kb_phylogenomics._view_genome_circle_plot_submit", args, retType, true, true, jsonRpcContext);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: view_genome_circle_plot</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link kbphylogenomics.ViewGenomeCirclePlotInput ViewGenomeCirclePlotInput} (original type "view_genome_circle_plot_Input")
     * @return   parameter "output" of type {@link kbphylogenomics.ViewGenomeCirclePlotOutput ViewGenomeCirclePlotOutput} (original type "view_genome_circle_plot_Output")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public ViewGenomeCirclePlotOutput viewGenomeCirclePlot(ViewGenomeCirclePlotInput params, RpcContext... jsonRpcContext) throws IOException, JsonClientException {
        String jobId = _viewGenomeCirclePlotSubmit(params, jsonRpcContext);
        TypeReference<List<JobState<List<ViewGenomeCirclePlotOutput>>>> retType = new TypeReference<List<JobState<List<ViewGenomeCirclePlotOutput>>>>() {};
        long asyncJobCheckTimeMs = this.asyncJobCheckTimeMs;
        while (true) {
            if (Thread.currentThread().isInterrupted())
                throw new JsonClientException("Thread was interrupted");
            try { 
                Thread.sleep(asyncJobCheckTimeMs);
            } catch(Exception ex) {
                throw new JsonClientException("Thread was interrupted", ex);
            }
            asyncJobCheckTimeMs = Math.min(asyncJobCheckTimeMs * this.asyncJobCheckTimeScalePercent / 100, this.asyncJobCheckMaxTimeMs);
            JobState<List<ViewGenomeCirclePlotOutput>> res = _checkJob(jobId, retType);
            if (res.getFinished() != 0L)
                return res.getResult().get(0);
        }
    }

    /**
     * <p>Original spec-file function name: view_pan_circle_plot</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link kbphylogenomics.ViewPanCirclePlotInput ViewPanCirclePlotInput} (original type "view_pan_circle_plot_Input")
     * @return   parameter "output" of type {@link kbphylogenomics.ViewPanCirclePlotOutput ViewPanCirclePlotOutput} (original type "view_pan_circle_plot_Output")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    protected String _viewPanCirclePlotSubmit(ViewPanCirclePlotInput params, RpcContext... jsonRpcContext) throws IOException, JsonClientException {
        if (this.serviceVersion != null) {
            if (jsonRpcContext == null || jsonRpcContext.length == 0 || jsonRpcContext[0] == null)
                jsonRpcContext = new RpcContext[] {new RpcContext()};
            jsonRpcContext[0].getAdditionalProperties().put("service_ver", this.serviceVersion);
        }
        List<Object> args = new ArrayList<Object>();
        args.add(params);
        TypeReference<List<String>> retType = new TypeReference<List<String>>() {};
        List<String> res = caller.jsonrpcCall("kb_phylogenomics._view_pan_circle_plot_submit", args, retType, true, true, jsonRpcContext);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: view_pan_circle_plot</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link kbphylogenomics.ViewPanCirclePlotInput ViewPanCirclePlotInput} (original type "view_pan_circle_plot_Input")
     * @return   parameter "output" of type {@link kbphylogenomics.ViewPanCirclePlotOutput ViewPanCirclePlotOutput} (original type "view_pan_circle_plot_Output")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public ViewPanCirclePlotOutput viewPanCirclePlot(ViewPanCirclePlotInput params, RpcContext... jsonRpcContext) throws IOException, JsonClientException {
        String jobId = _viewPanCirclePlotSubmit(params, jsonRpcContext);
        TypeReference<List<JobState<List<ViewPanCirclePlotOutput>>>> retType = new TypeReference<List<JobState<List<ViewPanCirclePlotOutput>>>>() {};
        long asyncJobCheckTimeMs = this.asyncJobCheckTimeMs;
        while (true) {
            if (Thread.currentThread().isInterrupted())
                throw new JsonClientException("Thread was interrupted");
            try { 
                Thread.sleep(asyncJobCheckTimeMs);
            } catch(Exception ex) {
                throw new JsonClientException("Thread was interrupted", ex);
            }
            asyncJobCheckTimeMs = Math.min(asyncJobCheckTimeMs * this.asyncJobCheckTimeScalePercent / 100, this.asyncJobCheckMaxTimeMs);
            JobState<List<ViewPanCirclePlotOutput>> res = _checkJob(jobId, retType);
            if (res.getFinished() != 0L)
                return res.getResult().get(0);
        }
    }

    /**
     * <p>Original spec-file function name: view_pan_accumulation_plot</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link kbphylogenomics.ViewPanAccumulationPlotInput ViewPanAccumulationPlotInput} (original type "view_pan_accumulation_plot_Input")
     * @return   parameter "output" of type {@link kbphylogenomics.ViewPanAccumulationPlotOutput ViewPanAccumulationPlotOutput} (original type "view_pan_accumulation_plot_Output")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    protected String _viewPanAccumulationPlotSubmit(ViewPanAccumulationPlotInput params, RpcContext... jsonRpcContext) throws IOException, JsonClientException {
        if (this.serviceVersion != null) {
            if (jsonRpcContext == null || jsonRpcContext.length == 0 || jsonRpcContext[0] == null)
                jsonRpcContext = new RpcContext[] {new RpcContext()};
            jsonRpcContext[0].getAdditionalProperties().put("service_ver", this.serviceVersion);
        }
        List<Object> args = new ArrayList<Object>();
        args.add(params);
        TypeReference<List<String>> retType = new TypeReference<List<String>>() {};
        List<String> res = caller.jsonrpcCall("kb_phylogenomics._view_pan_accumulation_plot_submit", args, retType, true, true, jsonRpcContext);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: view_pan_accumulation_plot</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link kbphylogenomics.ViewPanAccumulationPlotInput ViewPanAccumulationPlotInput} (original type "view_pan_accumulation_plot_Input")
     * @return   parameter "output" of type {@link kbphylogenomics.ViewPanAccumulationPlotOutput ViewPanAccumulationPlotOutput} (original type "view_pan_accumulation_plot_Output")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public ViewPanAccumulationPlotOutput viewPanAccumulationPlot(ViewPanAccumulationPlotInput params, RpcContext... jsonRpcContext) throws IOException, JsonClientException {
        String jobId = _viewPanAccumulationPlotSubmit(params, jsonRpcContext);
        TypeReference<List<JobState<List<ViewPanAccumulationPlotOutput>>>> retType = new TypeReference<List<JobState<List<ViewPanAccumulationPlotOutput>>>>() {};
        long asyncJobCheckTimeMs = this.asyncJobCheckTimeMs;
        while (true) {
            if (Thread.currentThread().isInterrupted())
                throw new JsonClientException("Thread was interrupted");
            try { 
                Thread.sleep(asyncJobCheckTimeMs);
            } catch(Exception ex) {
                throw new JsonClientException("Thread was interrupted", ex);
            }
            asyncJobCheckTimeMs = Math.min(asyncJobCheckTimeMs * this.asyncJobCheckTimeScalePercent / 100, this.asyncJobCheckMaxTimeMs);
            JobState<List<ViewPanAccumulationPlotOutput>> res = _checkJob(jobId, retType);
            if (res.getFinished() != 0L)
                return res.getResult().get(0);
        }
    }

    /**
     * <p>Original spec-file function name: view_pan_flower_venn</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link kbphylogenomics.ViewPanFlowerVennInput ViewPanFlowerVennInput} (original type "view_pan_flower_venn_Input")
     * @return   parameter "output" of type {@link kbphylogenomics.ViewPanFlowerVennOutput ViewPanFlowerVennOutput} (original type "view_pan_flower_venn_Output")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    protected String _viewPanFlowerVennSubmit(ViewPanFlowerVennInput params, RpcContext... jsonRpcContext) throws IOException, JsonClientException {
        if (this.serviceVersion != null) {
            if (jsonRpcContext == null || jsonRpcContext.length == 0 || jsonRpcContext[0] == null)
                jsonRpcContext = new RpcContext[] {new RpcContext()};
            jsonRpcContext[0].getAdditionalProperties().put("service_ver", this.serviceVersion);
        }
        List<Object> args = new ArrayList<Object>();
        args.add(params);
        TypeReference<List<String>> retType = new TypeReference<List<String>>() {};
        List<String> res = caller.jsonrpcCall("kb_phylogenomics._view_pan_flower_venn_submit", args, retType, true, true, jsonRpcContext);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: view_pan_flower_venn</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link kbphylogenomics.ViewPanFlowerVennInput ViewPanFlowerVennInput} (original type "view_pan_flower_venn_Input")
     * @return   parameter "output" of type {@link kbphylogenomics.ViewPanFlowerVennOutput ViewPanFlowerVennOutput} (original type "view_pan_flower_venn_Output")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public ViewPanFlowerVennOutput viewPanFlowerVenn(ViewPanFlowerVennInput params, RpcContext... jsonRpcContext) throws IOException, JsonClientException {
        String jobId = _viewPanFlowerVennSubmit(params, jsonRpcContext);
        TypeReference<List<JobState<List<ViewPanFlowerVennOutput>>>> retType = new TypeReference<List<JobState<List<ViewPanFlowerVennOutput>>>>() {};
        long asyncJobCheckTimeMs = this.asyncJobCheckTimeMs;
        while (true) {
            if (Thread.currentThread().isInterrupted())
                throw new JsonClientException("Thread was interrupted");
            try { 
                Thread.sleep(asyncJobCheckTimeMs);
            } catch(Exception ex) {
                throw new JsonClientException("Thread was interrupted", ex);
            }
            asyncJobCheckTimeMs = Math.min(asyncJobCheckTimeMs * this.asyncJobCheckTimeScalePercent / 100, this.asyncJobCheckMaxTimeMs);
            JobState<List<ViewPanFlowerVennOutput>> res = _checkJob(jobId, retType);
            if (res.getFinished() != 0L)
                return res.getResult().get(0);
        }
    }

    /**
     * <p>Original spec-file function name: view_pan_pairwise_overlap</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link kbphylogenomics.ViewPanPairwiseOverlapInput ViewPanPairwiseOverlapInput} (original type "view_pan_pairwise_overlap_Input")
     * @return   parameter "output" of type {@link kbphylogenomics.ViewPanPairwiseOverlapOutput ViewPanPairwiseOverlapOutput} (original type "view_pan_pairwise_overlap_Output")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    protected String _viewPanPairwiseOverlapSubmit(ViewPanPairwiseOverlapInput params, RpcContext... jsonRpcContext) throws IOException, JsonClientException {
        if (this.serviceVersion != null) {
            if (jsonRpcContext == null || jsonRpcContext.length == 0 || jsonRpcContext[0] == null)
                jsonRpcContext = new RpcContext[] {new RpcContext()};
            jsonRpcContext[0].getAdditionalProperties().put("service_ver", this.serviceVersion);
        }
        List<Object> args = new ArrayList<Object>();
        args.add(params);
        TypeReference<List<String>> retType = new TypeReference<List<String>>() {};
        List<String> res = caller.jsonrpcCall("kb_phylogenomics._view_pan_pairwise_overlap_submit", args, retType, true, true, jsonRpcContext);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: view_pan_pairwise_overlap</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link kbphylogenomics.ViewPanPairwiseOverlapInput ViewPanPairwiseOverlapInput} (original type "view_pan_pairwise_overlap_Input")
     * @return   parameter "output" of type {@link kbphylogenomics.ViewPanPairwiseOverlapOutput ViewPanPairwiseOverlapOutput} (original type "view_pan_pairwise_overlap_Output")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public ViewPanPairwiseOverlapOutput viewPanPairwiseOverlap(ViewPanPairwiseOverlapInput params, RpcContext... jsonRpcContext) throws IOException, JsonClientException {
        String jobId = _viewPanPairwiseOverlapSubmit(params, jsonRpcContext);
        TypeReference<List<JobState<List<ViewPanPairwiseOverlapOutput>>>> retType = new TypeReference<List<JobState<List<ViewPanPairwiseOverlapOutput>>>>() {};
        long asyncJobCheckTimeMs = this.asyncJobCheckTimeMs;
        while (true) {
            if (Thread.currentThread().isInterrupted())
                throw new JsonClientException("Thread was interrupted");
            try { 
                Thread.sleep(asyncJobCheckTimeMs);
            } catch(Exception ex) {
                throw new JsonClientException("Thread was interrupted", ex);
            }
            asyncJobCheckTimeMs = Math.min(asyncJobCheckTimeMs * this.asyncJobCheckTimeScalePercent / 100, this.asyncJobCheckMaxTimeMs);
            JobState<List<ViewPanPairwiseOverlapOutput>> res = _checkJob(jobId, retType);
            if (res.getFinished() != 0L)
                return res.getResult().get(0);
        }
    }

    /**
     * <p>Original spec-file function name: view_pan_phylo</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link kbphylogenomics.ViewPanPhyloInput ViewPanPhyloInput} (original type "view_pan_phylo_Input")
     * @return   parameter "output" of type {@link kbphylogenomics.ViewPanPhyloOutput ViewPanPhyloOutput} (original type "view_pan_phylo_Output")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    protected String _viewPanPhyloSubmit(ViewPanPhyloInput params, RpcContext... jsonRpcContext) throws IOException, JsonClientException {
        if (this.serviceVersion != null) {
            if (jsonRpcContext == null || jsonRpcContext.length == 0 || jsonRpcContext[0] == null)
                jsonRpcContext = new RpcContext[] {new RpcContext()};
            jsonRpcContext[0].getAdditionalProperties().put("service_ver", this.serviceVersion);
        }
        List<Object> args = new ArrayList<Object>();
        args.add(params);
        TypeReference<List<String>> retType = new TypeReference<List<String>>() {};
        List<String> res = caller.jsonrpcCall("kb_phylogenomics._view_pan_phylo_submit", args, retType, true, true, jsonRpcContext);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: view_pan_phylo</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link kbphylogenomics.ViewPanPhyloInput ViewPanPhyloInput} (original type "view_pan_phylo_Input")
     * @return   parameter "output" of type {@link kbphylogenomics.ViewPanPhyloOutput ViewPanPhyloOutput} (original type "view_pan_phylo_Output")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public ViewPanPhyloOutput viewPanPhylo(ViewPanPhyloInput params, RpcContext... jsonRpcContext) throws IOException, JsonClientException {
        String jobId = _viewPanPhyloSubmit(params, jsonRpcContext);
        TypeReference<List<JobState<List<ViewPanPhyloOutput>>>> retType = new TypeReference<List<JobState<List<ViewPanPhyloOutput>>>>() {};
        long asyncJobCheckTimeMs = this.asyncJobCheckTimeMs;
        while (true) {
            if (Thread.currentThread().isInterrupted())
                throw new JsonClientException("Thread was interrupted");
            try { 
                Thread.sleep(asyncJobCheckTimeMs);
            } catch(Exception ex) {
                throw new JsonClientException("Thread was interrupted", ex);
            }
            asyncJobCheckTimeMs = Math.min(asyncJobCheckTimeMs * this.asyncJobCheckTimeScalePercent / 100, this.asyncJobCheckMaxTimeMs);
            JobState<List<ViewPanPhyloOutput>> res = _checkJob(jobId, retType);
            if (res.getFinished() != 0L)
                return res.getResult().get(0);
        }
    }

    /**
     * <p>Original spec-file function name: find_homologs_with_genome_context</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link kbphylogenomics.FindHomologsWithGenomeContextInput FindHomologsWithGenomeContextInput} (original type "find_homologs_with_genome_context_Input")
     * @return   parameter "output" of type {@link kbphylogenomics.FindHomologsWithGenomeContextOutput FindHomologsWithGenomeContextOutput} (original type "find_homologs_with_genome_context_Output")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    protected String _findHomologsWithGenomeContextSubmit(FindHomologsWithGenomeContextInput params, RpcContext... jsonRpcContext) throws IOException, JsonClientException {
        if (this.serviceVersion != null) {
            if (jsonRpcContext == null || jsonRpcContext.length == 0 || jsonRpcContext[0] == null)
                jsonRpcContext = new RpcContext[] {new RpcContext()};
            jsonRpcContext[0].getAdditionalProperties().put("service_ver", this.serviceVersion);
        }
        List<Object> args = new ArrayList<Object>();
        args.add(params);
        TypeReference<List<String>> retType = new TypeReference<List<String>>() {};
        List<String> res = caller.jsonrpcCall("kb_phylogenomics._find_homologs_with_genome_context_submit", args, retType, true, true, jsonRpcContext);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: find_homologs_with_genome_context</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link kbphylogenomics.FindHomologsWithGenomeContextInput FindHomologsWithGenomeContextInput} (original type "find_homologs_with_genome_context_Input")
     * @return   parameter "output" of type {@link kbphylogenomics.FindHomologsWithGenomeContextOutput FindHomologsWithGenomeContextOutput} (original type "find_homologs_with_genome_context_Output")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public FindHomologsWithGenomeContextOutput findHomologsWithGenomeContext(FindHomologsWithGenomeContextInput params, RpcContext... jsonRpcContext) throws IOException, JsonClientException {
        String jobId = _findHomologsWithGenomeContextSubmit(params, jsonRpcContext);
        TypeReference<List<JobState<List<FindHomologsWithGenomeContextOutput>>>> retType = new TypeReference<List<JobState<List<FindHomologsWithGenomeContextOutput>>>>() {};
        long asyncJobCheckTimeMs = this.asyncJobCheckTimeMs;
        while (true) {
            if (Thread.currentThread().isInterrupted())
                throw new JsonClientException("Thread was interrupted");
            try { 
                Thread.sleep(asyncJobCheckTimeMs);
            } catch(Exception ex) {
                throw new JsonClientException("Thread was interrupted", ex);
            }
            asyncJobCheckTimeMs = Math.min(asyncJobCheckTimeMs * this.asyncJobCheckTimeScalePercent / 100, this.asyncJobCheckMaxTimeMs);
            JobState<List<FindHomologsWithGenomeContextOutput>> res = _checkJob(jobId, retType);
            if (res.getFinished() != 0L)
                return res.getResult().get(0);
        }
    }

    /**
     * <p>Original spec-file function name: get_configure_categories</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link kbphylogenomics.GetConfigureCategoriesInput GetConfigureCategoriesInput} (original type "get_configure_categories_Input")
     * @return   parameter "output" of type {@link kbphylogenomics.GetConfigureCategoriesOutput GetConfigureCategoriesOutput} (original type "get_configure_categories_Output")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    protected String _getConfigureCategoriesSubmit(GetConfigureCategoriesInput params, RpcContext... jsonRpcContext) throws IOException, JsonClientException {
        if (this.serviceVersion != null) {
            if (jsonRpcContext == null || jsonRpcContext.length == 0 || jsonRpcContext[0] == null)
                jsonRpcContext = new RpcContext[] {new RpcContext()};
            jsonRpcContext[0].getAdditionalProperties().put("service_ver", this.serviceVersion);
        }
        List<Object> args = new ArrayList<Object>();
        args.add(params);
        TypeReference<List<String>> retType = new TypeReference<List<String>>() {};
        List<String> res = caller.jsonrpcCall("kb_phylogenomics._get_configure_categories_submit", args, retType, true, true, jsonRpcContext);
        return res.get(0);
    }

    /**
     * <p>Original spec-file function name: get_configure_categories</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link kbphylogenomics.GetConfigureCategoriesInput GetConfigureCategoriesInput} (original type "get_configure_categories_Input")
     * @return   parameter "output" of type {@link kbphylogenomics.GetConfigureCategoriesOutput GetConfigureCategoriesOutput} (original type "get_configure_categories_Output")
     * @throws IOException if an IO exception occurs
     * @throws JsonClientException if a JSON RPC exception occurs
     */
    public GetConfigureCategoriesOutput getConfigureCategories(GetConfigureCategoriesInput params, RpcContext... jsonRpcContext) throws IOException, JsonClientException {
        String jobId = _getConfigureCategoriesSubmit(params, jsonRpcContext);
        TypeReference<List<JobState<List<GetConfigureCategoriesOutput>>>> retType = new TypeReference<List<JobState<List<GetConfigureCategoriesOutput>>>>() {};
        long asyncJobCheckTimeMs = this.asyncJobCheckTimeMs;
        while (true) {
            if (Thread.currentThread().isInterrupted())
                throw new JsonClientException("Thread was interrupted");
            try { 
                Thread.sleep(asyncJobCheckTimeMs);
            } catch(Exception ex) {
                throw new JsonClientException("Thread was interrupted", ex);
            }
            asyncJobCheckTimeMs = Math.min(asyncJobCheckTimeMs * this.asyncJobCheckTimeScalePercent / 100, this.asyncJobCheckMaxTimeMs);
            JobState<List<GetConfigureCategoriesOutput>> res = _checkJob(jobId, retType);
            if (res.getFinished() != 0L)
                return res.getResult().get(0);
        }
    }

    public Map<String, Object> status(RpcContext... jsonRpcContext) throws IOException, JsonClientException {
        if (this.serviceVersion != null) {
            if (jsonRpcContext == null || jsonRpcContext.length == 0 || jsonRpcContext[0] == null)
                jsonRpcContext = new RpcContext[] {new RpcContext()};
            jsonRpcContext[0].getAdditionalProperties().put("service_ver", this.serviceVersion);
        }
        List<Object> args = new ArrayList<Object>();
        TypeReference<List<String>> retType1 = new TypeReference<List<String>>() {};
        List<String> res1 = caller.jsonrpcCall("kb_phylogenomics._status_submit", args, retType1, true, true, jsonRpcContext);
        String jobId = res1.get(0);
        TypeReference<List<JobState<List<Map<String, Object>>>>> retType2 = new TypeReference<List<JobState<List<Map<String, Object>>>>>() {};
        long asyncJobCheckTimeMs = this.asyncJobCheckTimeMs;
        while (true) {
            if (Thread.currentThread().isInterrupted())
                throw new JsonClientException("Thread was interrupted");
            try { 
                Thread.sleep(asyncJobCheckTimeMs);
            } catch(Exception ex) {
                throw new JsonClientException("Thread was interrupted", ex);
            }
            asyncJobCheckTimeMs = Math.min(asyncJobCheckTimeMs * this.asyncJobCheckTimeScalePercent / 100, this.asyncJobCheckMaxTimeMs);
            JobState<List<Map<String, Object>>> res2 = _checkJob(jobId, retType2);
            if (res2.getFinished() != 0L)
                return res2.getResult().get(0);
        }
    }
}
