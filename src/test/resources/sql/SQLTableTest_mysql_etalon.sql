/* ========================================================================== */
/*   Generation Automatique : gen.Dividend   */
/* ========================================================================== */

${dropTableScript(AP_DIVIDEND)}

/* ========================================================================== */
create table AP_DIVIDEND(
    OPERATION_CODE  numeric(23)  auto_increment not null,
    MODEL text null ,
    primary key (OPERATION_CODE) ) ENGINE=InnoDB${queryDelimiter}

/* ========================================================================== */

${logTableCreationScript(AP_DIVIDEND)}
