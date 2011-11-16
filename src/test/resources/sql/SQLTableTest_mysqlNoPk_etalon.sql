/* ========================================================================== */
/*   Generation Automatique : gen.Dividend   */
/* ========================================================================== */

${dropTableScript(AP_DIVIDEND)}

/* ========================================================================== */
create table AP_DIVIDEND(
    OPERATION_CODE  numeric(23) not null,
    MODEL text null ) ENGINE=InnoDB${queryDelimiter}

/* ========================================================================== */

${logTableCreationScript(AP_DIVIDEND)}
