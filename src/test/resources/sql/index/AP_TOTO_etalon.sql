/*=============================*/
/*   Suppression des indexes   */
/*=============================*/

${dropIndexScript(AP_TOTO.X1_AP_TOTO)}

${dropIndexScript(AP_TOTO.X2_AP_TOTO)}

${dropIndexScript(AP_TOTO.X3_AP_TOTO)}


/*==========================*/
/*   Creation des indexes   */
/*==========================*/

${createIndexScript(AP_TOTO.X1_AP_TOTO)}

${createIndexScript(AP_TOTO.X2_AP_TOTO)}

${createIndexScript(AP_TOTO.X3_AP_TOTO)}


/*==============================*/
/*   Verification des indexes   */
/*==============================*/

${logIndexCreationScript(AP_TOTO.X1_AP_TOTO)}

${logIndexCreationScript(AP_TOTO.X2_AP_TOTO)}

${logIndexCreationScript(AP_TOTO.X3_AP_TOTO)}
