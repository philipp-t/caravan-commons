/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2014 wcm.io
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
package io.wcm.caravan.commons.hal;

import io.wcm.caravan.commons.hal.domain.HalResource;
import io.wcm.caravan.commons.hal.domain.Link;
import io.wcm.caravan.commons.hal.mapper.ResourceMapper;
import io.wcm.caravan.commons.stream.Collectors;
import io.wcm.caravan.commons.stream.Streams;

import java.util.List;

/**
 * Factory for HAL {@link HalResource}s.
 */
public final class HalResourceFactory {

  private HalResourceFactory() {
    // nothing to do
  }

  /**
   * Creates a HAL resource without state but a self link. Mostly needed for index resources.
   * @param href The self HREF for the resource
   * @return New HAL resource
   */
  public static HalResource createResource(final String href) {
    return new HalResource().setLink("self", new Link(href));
  }

  /**
   * Creates a HAL resource with state and a self link.
   * @param state The state of the resource
   * @param href The self link for the resource
   * @return New HAL resource
   */
  public static HalResource createResource(final Object state, final String href) {
    return new HalResource().setState(state).setLink("self", new Link(href));
  }

  /**
   * Creates a HAL resource with state and self link generated by the mapper from the input object. Uses
   * {@link ResourceMapper#getResource(Object)} to define the state.
   * @param input The object to map to state and self link
   * @param mapper The resource mapper getting applied on the input object
   * @return New HAL resource
   */
  public static HalResource createResource(final Object input, final ResourceMapper<?, ?> mapper) {
    @SuppressWarnings("unchecked")
    ResourceMapper<Object, ?> castedMapper = (ResourceMapper<Object, ?>)mapper;
    return createResource(castedMapper.getResource(input), castedMapper.getHref(input));
  }

  /**
   * Creates an embedded resource with state and self link generated by the mapper from the input object. Uses
   * {@link ResourceMapper#getEmbeddedResource(Object)} to define the state.
   * @param input The object to map to state and self link
   * @param mapper The resource mapper getting applied on the input object
   * @return New HAL resource
   */
  public static HalResource createEmbeddedResource(final Object input, final ResourceMapper<?, ?> mapper) {
    @SuppressWarnings("unchecked")
    ResourceMapper<Object, ?> castedMapper = (ResourceMapper<Object, ?>)mapper;
    return createResource(castedMapper.getEmbeddedResource(input), castedMapper.getHref(input));
  }

  /**
   * Creates multiple embedded resources with state and self link generated by the mapper from the input objects. Uses
   * {@link ResourceMapper#getEmbeddedResource(Object)} to define the state.
   * @param inputs The objects to map to state and self link
   * @param mapper The resource mapper getting applied on the input objects
   * @return New HAL resources
   */
  public static List<HalResource> createEmbeddedResources(final Iterable<?> inputs, final ResourceMapper<?, ?> mapper) {
    @SuppressWarnings("unchecked")
    ResourceMapper<Object, ?> castedMapper = (ResourceMapper<Object, ?>)mapper;
    return Streams.of(inputs)
        .map(e -> createResource(castedMapper.getEmbeddedResource(e), castedMapper.getHref(e)))
        .collect(Collectors.toList());
  }

}
