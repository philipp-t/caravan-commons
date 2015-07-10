/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2015 wcm.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.wcm.caravan.commons.haldocs.impl;

import io.wcm.caravan.commons.jaxrs.ApplicationPath;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tracks bundles that contain HAL documentation files generated by hal-docs-maven-plugin, and mounts them
 * via HTTP service to /docs/api/{serviceId}/*.
 */
@Component(immediate = true)
public class HalDocsBundleTracker implements BundleTrackerCustomizer<String> {

  static final String DOCS_URI_PREFIX = "/docs/api";
  static final String DOCS_CLASSPATH_PREFIX = "HAL-DOCS-INF";

  private static final Logger log = LoggerFactory.getLogger(HalDocsBundleTracker.class);

  private BundleContext bundleContext;
  private BundleTracker bundleTracker;

  @Reference
  private HttpService httpService;

  @Activate
  void activate(ComponentContext componentContext) {
    bundleContext = componentContext.getBundleContext();
    this.bundleTracker = new BundleTracker<String>(bundleContext, Bundle.ACTIVE, this);
    this.bundleTracker.open();
  }

  @Deactivate
  void deactivate(ComponentContext componentContext) {
    this.bundleTracker.close();
  }

  @Override
  public String addingBundle(Bundle bundle, BundleEvent event) {
    String applicationPath = ApplicationPath.get(bundle);
    if (StringUtils.isNotBlank(applicationPath) && hasHalDocs(bundle)) {
      String docsPath = getDocsPath(applicationPath);

      if (log.isInfoEnabled()) {
        log.info("Mount HAL docs for {} to {}", bundle.getSymbolicName(), docsPath);
      }

      try {
        httpService.registerResources(docsPath, DOCS_CLASSPATH_PREFIX,
            new HttpContextWrapper(httpService.createDefaultHttpContext(), bundle));
      }
      catch (NamespaceException ex) {
        throw new RuntimeException("Unable to mount hal docs to " + docsPath, ex);
      }
      return docsPath;
    }
    return null;
  }

  @Override
  public void modifiedBundle(Bundle bundle, BundleEvent event, String docsPath) {
    // nothing to do
  }

  @Override
  public void removedBundle(Bundle bundle, BundleEvent event, String docsPath) {
    if (docsPath == null) {
      return;
    }
    if (log.isInfoEnabled()) {
      log.info("Unmount HAL docs for {} from {}", bundle.getSymbolicName(), docsPath);
    }
    httpService.unregister(docsPath);
  }

  private boolean hasHalDocs(Bundle bundle) {
    // TODO: check bundle contents if documentation artifacts exist
    return true;
  }

  private String getDocsPath(String applicationPath) {
    return DOCS_URI_PREFIX + applicationPath;
  }

}
