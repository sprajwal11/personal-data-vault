package com.vaultapp.personal_data_vault.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogAudit {
    String action();
}
