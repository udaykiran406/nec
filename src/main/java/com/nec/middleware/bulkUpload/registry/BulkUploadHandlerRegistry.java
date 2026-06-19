package com.nec.middleware.bulkUpload.registry;


import com.nec.middleware.bulkUpload.handler.BulkUploadHandler;
import com.nec.middleware.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Maps a module name (the {@code {module}} path variable on
 * {@code POST /bulk-upload/{module}}) to the {@link BulkUploadHandler} bean
 * responsible for it.
 *
 * <p>Spring injects every {@code BulkUploadHandler} bean in the application
 * context automatically — there is no manual wiring list to maintain here.
 * Adding a new module is exactly: write a handler class, annotate it
 * {@code @Component}, give it a unique {@link BulkUploadHandler#moduleName()}.
 * It shows up in this registry automatically on next startup.
 *
 * <p>Fails fast at startup if two handlers claim the same module name,
 * rather than silently letting one shadow the other at request time.
 */
@Slf4j
@Component
public class BulkUploadHandlerRegistry {

    private final Map<String, BulkUploadHandler<?, ?>> handlersByModule;

    public BulkUploadHandlerRegistry(List<BulkUploadHandler<?, ?>> handlers) {
        this.handlersByModule = handlers.stream()
                .collect(Collectors.toMap(
                        h -> normalize(h.moduleName()),
                        h -> h,
                        (existing, duplicate) -> {
                            throw new IllegalStateException(
                                    "Duplicate bulk upload handler registered for module '"
                                            + existing.moduleName() + "': "
                                            + existing.getClass().getSimpleName()
                                            + " and " + duplicate.getClass().getSimpleName());
                        }));

        log.info("Registered {} bulk upload handler(s): {}",
                handlersByModule.size(), handlersByModule.keySet());
    }

    /**
     * Look up the handler for {@code module} (case-insensitive).
     *
     * @throws BadRequestException if no handler is registered for the given module name
     */
    public BulkUploadHandler<?, ?> getHandler(String module) {
        BulkUploadHandler<?, ?> handler = handlersByModule.get(normalize(module));
        if (handler == null) {
            throw new BadRequestException(
                    "Unsupported bulk upload module '" + module + "'. Supported modules: "
                            + handlersByModule.keySet());
        }
        return handler;
    }


    private String normalize(String moduleName) {
        return moduleName == null ? "" : moduleName.trim().toUpperCase();
    }
}
