/* Copyright (c) pro!vision GmbH. All rights reserved. */
package io.wcm.caravan.commons.hal;

import io.wcm.caravan.commons.hal.domain.HalResource;
import io.wcm.caravan.commons.hal.domain.Link;
import io.wcm.caravan.commons.hal.mapper.ResourceMapper;

import java.util.List;

/**
 * Short named helper for HAL resources.
 */
public class HAL {

  private final HalResource instance;

  /**
   * @see HalResourceFactory#createResource(String)
   * @param href Link HREF
   */
  public HAL(String href) {
    instance = HalResourceFactory.createResource(href);
  }

  /**
   * @see HalResourceFactory#createResource(Object, String)
   * @param state Resource state
   * @param href Link HREF
   */
  public HAL(Object state, String href) {
    instance = HalResourceFactory.createResource(state, href);
  }

  /**
   * @see HalResourceFactory#createResource(Object, ResourceMapper)
   * @param input Resource pre-mapped state
   * @param mapper Resource state mapper
   */
  public HAL(Object input, ResourceMapper<?, ?> mapper) {
    instance = HalResourceFactory.createResource(input, mapper);
  }

  /**
   * @see HalResourceFactory#createEmbeddedResource(Object, ResourceMapper)
   * @see HalResource#setEmbeddedResource(String, HalResource)
   * @param name Embedded resource name
   * @param input Embedded resource pre-mapped state
   * @param mapper Embedded resource state mapper
   * @return Helper
   */
  public HAL embed(String name, Object input, ResourceMapper<?, ?> mapper) {
    HalResource embeddedResource = HalResourceFactory.createEmbeddedResource(input, mapper);
    instance.setEmbeddedResource(name, embeddedResource);
    return this;
  }

  /**
   * @see HalResourceFactory#createEmbeddedResources(Iterable, ResourceMapper)
   * @see HalResource#setEmbeddedResource(String, List)
   * @param name Embedded resources name
   * @param inputs Embedded resources pre-mapped state
   * @param mapper Embedded resources state mapper
   * @return Helper
   */
  public HAL embedAll(String name, Iterable<?> inputs, ResourceMapper<?, ?> mapper) {
    List<HalResource> embeddedResource = HalResourceFactory.createEmbeddedResources(inputs, mapper);
    instance.setEmbeddedResource(name, embeddedResource);
    return this;
  }

  /**
   * @see HalResource#setLink(String, Link)
   * @param relation Link relation
   * @param href Link HREF
   * @return Helper
   */
  public HAL link(String relation, String href) {
    instance.setLink(relation, new Link(href));
    return this;
  }

  /**
   * @return The HAL resource
   */
  public HalResource get() {
    return instance;
  }

}
