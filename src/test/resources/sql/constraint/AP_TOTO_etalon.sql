/*=================================*/
/*   Suppression des contraintes   */
/*=================================*/

${dropConstraintScript(AP_TOTO.FK_MERETOTO_TOTO)}

${dropConstraintScript(AP_TOTO.FK_MERETOTO2_TOTO)}


/*==============================*/
/*   Creation des contraintes   */
/*==============================*/

${createConstraintScript(AP_TOTO.FK_MERETOTO_TOTO)}

${createConstraintScript(AP_TOTO.FK_MERETOTO2_TOTO)}


/*==================================*/
/*   Verification des contraintes   */
/*==================================*/

${logConstraintCreationScript(AP_TOTO.FK_MERETOTO_TOTO)}

${logConstraintCreationScript(AP_TOTO.FK_MERETOTO2_TOTO)}
