package org.mortbay.ijetty.firstblh;/**
 * Created by firstblh on 16/9/12.
 */

import org.eclipse.jetty.deploy.providers.WebAppProvider;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.AttributesMap;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * DATE: 2016-09-12
 * TIME: 15:40
 * CREATOR: FIRSTBLH@163.com
 */
public class AndroidWebAppDeployer extends WebAppProvider {
    private List<? super ServletContextHandler> _deployed;
    private AttributesMap _attributes = new AttributesMap();
    /**
     * modify by firstblh
     */
    private String _webAppDir;
    private HandlerCollection _contexts;
    private boolean _allowDuplicates;
    private boolean _extract;

    public AndroidWebAppDeployer() {
    }

    public void doStart() throws Exception {
        this._deployed = new ArrayList();
        this.scan();
    }

    public void doStop() throws Exception {
        int i = this._deployed.size();

        while(i-- > 0) {
            ContextHandler wac = (ContextHandler)this._deployed.get(i);
            wac.stop();
        }

    }

    public Object getAttribute(String name) {
        return this._attributes.getAttribute(name);
    }

    public void scan() throws Exception {
        if(this.getContexts() == null) {
            throw new IllegalArgumentException("No HandlerContainer");
        } else {
            Resource r = Resource.newResource(this.getWebAppDir());
            if(!r.exists()) {
                throw new IllegalArgumentException("No such webapps resource " + r);
            } else if(!r.isDirectory()) {
                throw new IllegalArgumentException("Not directory webapps resource " + r);
            } else {
                String[] files = r.list();

                label131:
                for(int f = 0; files != null && f < files.length; ++f) {
                    String context = files[f];
                    if(!context.equalsIgnoreCase("CVS/") && !context.equalsIgnoreCase("CVS") && !context.startsWith(".")) {
                        Resource app = r.addPath(r.encode(context));
                        Resource wah;
                        if(!context.toLowerCase().endsWith(".war") && !context.toLowerCase().endsWith(".jar")) {
                            if(!app.isDirectory()) {
                                Log.debug(app + " Not directory");
                                continue;
                            }
                        } else {
                            context = context.substring(0, context.length() - 4);
                            wah = r.addPath(context);
                            if(wah != null && wah.exists() && wah.isDirectory()) {
                                Log.debug(context + " already exists.");
                                continue;
                            }
                        }

                        if(!context.equalsIgnoreCase("root") && !context.equalsIgnoreCase("root/")) {
                            context = "/" + context;
                        } else {
                            context = "/";
                        }

                        if(context.endsWith("/") && context.length() > 0) {
                            context = context.substring(0, context.length() - 1);
                        }

                        String name;
                        if(!this.getAllowDuplicates()) {
                            Handler[] var11 = this.getContexts().getChildHandlersByClass(ContextHandler.class);

                            for(int contexts = 0; contexts < var11.length; ++contexts) {
                                ContextHandler names = (ContextHandler)var11[contexts];
                                if(context.equals(names.getContextPath())) {
                                    if(Log.isDebugEnabled()) {
                                        Log.debug(context + " Context were equal; duplicate!");
                                    }
                                    continue label131;
                                }

                                if(names instanceof WebAppContext) {
                                    name = Resource.newResource(((WebAppContext)names).getWar()).getFile().getAbsolutePath();
                                } else {
                                    name = names.getBaseResource().getFile().getAbsolutePath();
                                }

                                if(name != null && name.equals(app.getFile().getAbsolutePath())) {
                                    if(Log.isDebugEnabled()) {
                                        Log.debug(name + " Paths were equal; duplicate!");
                                    }
                                    continue label131;
                                }
                            }
                        }

                        wah = null;
                        HandlerCollection var13 = this.getContexts();
                        WebAppContext var12;
                        if(var13 instanceof ContextHandlerCollection && WebAppContext.class.isAssignableFrom(((ContextHandlerCollection)var13).getContextClass())) {
                            try {
                                var12 = (WebAppContext)((ContextHandlerCollection)var13).getContextClass().newInstance();
                            } catch (Exception var10) {
                                throw new Error(var10);
                            }
                        } else {
                            var12 = new WebAppContext();
                        }

                        var12.setContextPath(context);
                        if(this.getConfigurationClasses() != null) {
                            var12.setConfigurationClasses(this.getConfigurationClasses());
                        }

                        if(this.getDefaultsDescriptor() != null) {
                            var12.setDefaultsDescriptor(this.getDefaultsDescriptor());
                        }

                        var12.setExtractWAR(this.isExtract());
                        var12.setWar(app.toString());
                        var12.setParentLoaderPriority(this.isParentLoaderPriority());
                        Enumeration var14 = this._attributes.getAttributeNames();

                        while(var14.hasMoreElements()) {
                            name = (String)var14.nextElement();
                            var12.setAttribute(name, this._attributes.getAttribute(name));
                        }

                        Log.debug("AndroidWebAppDeployer: prepared " + app.toString());
                        var13.addHandler(var12);
                        this._deployed.add(var12);
                        var12.start();
                    }
                }

            }
        }
    }

    public void setAttribute(String name, Object value) {
        this._attributes.setAttribute(name, value);
    }


    // modify by firstblh

    public String getWebAppDir() {
        return this._webAppDir;
    }

    public void setWebAppDir(String dir) {
        this._webAppDir = dir;
    }

    public HandlerCollection getContexts() {
        return this._contexts;
    }

    public void setContexts(HandlerCollection contexts) {
        this._contexts = contexts;
    }

    public boolean getAllowDuplicates() {
        return this._allowDuplicates;
    }

    public void setAllowDuplicates(boolean allowDuplicates) {
        this._allowDuplicates = allowDuplicates;
    }

    public boolean isExtract() {
        return this._extract;
    }

    public void setExtract(boolean extract) {
        this._extract = extract;
    }
}
