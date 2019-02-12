package tomcat.context;

import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.ServerInfo;
import org.apache.catalina.util.TomcatCSS;
import org.apache.catalina.valves.Constants;
import org.apache.catalina.valves.ErrorReportValve;
import org.apache.coyote.ActionCode;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.Escape;

/**
 * host默认的错误页面重写，通过server.xml中host的属性errorReportValveClass指定
 * @author TD
 *
 */
public class MyErrorReportValveClass extends ErrorReportValve{
	/**
     * Prints out an error report.
     *
     * @param request The request being processed
     * @param response The response being generated
     * @param throwable The exception that occurred (which possibly wraps
     *  a root cause exception
     */
    protected void report(Request request, Response response, Throwable throwable) {
    	
        int statusCode = response.getStatus();

        // Do nothing on a 1xx, 2xx and 3xx status
        // Do nothing if anything has been written already
        // Do nothing if the response hasn't been explicitly marked as in error
        //    and that error has not been reported.
        if (statusCode < 400 || response.getContentWritten() > 0 || !response.setErrorReported()) {
            return;
        }

        // If an error has occurred that prevents further I/O, don't waste time
        // producing an error report that will never be read
        AtomicBoolean result = new AtomicBoolean(false);
        response.getCoyoteResponse().action(ActionCode.IS_IO_ALLOWED, result);
        if (!result.get()) {
            return;
        }

        String message = Escape.htmlElementContent(response.getMessage());
        if (message == null) {
            if (throwable != null) {
                String exceptionMessage = throwable.getMessage();
                if (exceptionMessage != null && exceptionMessage.length() > 0) {
                    message = Escape.htmlElementContent((new Scanner(exceptionMessage)).nextLine());
                }
            }
            if (message == null) {
                message = "";
            }
        }

        // Do nothing if there is no reason phrase for the specified status code and
        // no error message provided
        String reason = null;
        String description = null;
        StringManager smClient = StringManager.getManager(
                Constants.Package, request.getLocales());
        response.setLocale(smClient.getLocale());
        try {
            reason = smClient.getString("http." + statusCode + ".reason");
            description = smClient.getString("http." + statusCode + ".desc");
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
        }
        if (reason == null || description == null) {
            if (message.isEmpty()) {
                return;
            } else {
                reason = smClient.getString("errorReportValve.unknownReason");
                description = smClient.getString("errorReportValve.noDescription");
            }
        }

        StringBuilder sb = new StringBuilder();

        sb.append("<!doctype html><html lang=\"");
        sb.append(smClient.getLocale().getLanguage()).append("\">");
        sb.append("<head>");
        sb.append("<title>");
        /*sb.append(smClient.getString("errorReportValve.statusHeader",
                String.valueOf(statusCode), reason));
               */
        sb.append("老子是标题");
        sb.append("</title>");
        sb.append("<style type=\"text/css\">");
        sb.append(TomcatCSS.TOMCAT_CSS);
        sb.append("</style>");
        sb.append("</head><body>");
        sb.append("<h1>");
        sb.append(smClient.getString("errorReportValve.statusHeader",
                String.valueOf(statusCode), reason)).append("错误，晓得伐？</h1>");
        if (isShowReport()) {
            sb.append("<hr class=\"line\" />");
            sb.append("<p><b>");
            sb.append(smClient.getString("errorReportValve.type"));
            sb.append("</b> ");
            if (throwable != null) {
                sb.append(smClient.getString("errorReportValve.exceptionReport"));
            } else {
                sb.append(smClient.getString("errorReportValve.statusReport"));
            }
            sb.append("</p>");
            if (!message.isEmpty()) {
                sb.append("<p><b>");
                sb.append(smClient.getString("errorReportValve.message"));
                sb.append("</b> ");
                sb.append(message).append("</p>");
            }
            sb.append("<p><b>");
            sb.append(smClient.getString("errorReportValve.description"));
            sb.append("</b> ");
            sb.append(description);
            sb.append("</p>");
            if (throwable != null) {
                String stackTrace = getPartialServletStackTrace(throwable);
                sb.append("<p><b>");
                sb.append(smClient.getString("errorReportValve.exception"));
                sb.append("</b></p><pre>");
                sb.append(Escape.htmlElementContent(stackTrace));
                sb.append("</pre>");

                int loops = 0;
                Throwable rootCause = throwable.getCause();
                while (rootCause != null && (loops < 10)) {
                    stackTrace = getPartialServletStackTrace(rootCause);
                    sb.append("<p><b>");
                    sb.append(smClient.getString("errorReportValve.rootCause"));
                    sb.append("</b></p><pre>");
                    sb.append(Escape.htmlElementContent(stackTrace));
                    sb.append("</pre>");
                    // In case root cause is somehow heavily nested
                    rootCause = rootCause.getCause();
                    loops++;
                }

                sb.append("<p><b>");
                sb.append(smClient.getString("errorReportValve.note"));
                sb.append("</b> ");
                sb.append(smClient.getString("errorReportValve.rootCauseInLogs"));
                sb.append("</p>");

            }
            sb.append("<hr class=\"line\" />");
        }
        if (isShowServerInfo()) {
            sb.append("<h3>").append(ServerInfo.getServerInfo()).append("</h3>");
        }
        sb.append("</body></html>");

        try {
            try {
                response.setContentType("text/html");
                response.setCharacterEncoding("utf-8");
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                if (container.getLogger().isDebugEnabled()) {
                    container.getLogger().debug("status.setContentType", t);
                }
            }
            Writer writer = response.getReporter();
            if (writer != null) {
                // If writer is null, it's an indication that the response has
                // been hard committed already, which should never happen
                writer.write(sb.toString());
                response.finishResponse();
            }
        } catch (IOException e) {
            // Ignore
        } catch (IllegalStateException e) {
            // Ignore
        }

    }
}
