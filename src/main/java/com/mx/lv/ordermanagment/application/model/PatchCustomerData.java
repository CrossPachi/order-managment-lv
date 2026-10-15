package com.mx.lv.ordermanagment.application.model;

public record PatchCustomerData(
        String name,
        String fathersLastName,
        String mothersLastName,
        String email,
        String address
) {

    public boolean hasChanges() {
        return name != null
                || fathersLastName != null
                || mothersLastName != null
                || email != null
                || address != null;
    }
}

