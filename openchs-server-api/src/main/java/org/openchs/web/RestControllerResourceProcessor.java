package org.openchs.web;

import org.springframework.data.domain.Page;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;

public interface RestControllerResourceProcessor<T> {

    void process(Resource<T> tResource);

    default PagedResources<Resource<T>> wrap(Page<T> page) {
        PagedResources.PageMetadata pageMetadata = new PagedResources.PageMetadata(page.getSize(), page.getNumber(), page.getTotalElements(), page.getTotalPages());
        PagedResources<Resource<T>> paged = PagedResources.wrap(page, pageMetadata);
        paged.getContent().forEach(this::process);
        return paged;
    }
}
