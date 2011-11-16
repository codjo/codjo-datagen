/*=================================*/
/*   Suppression des contraintes   */
/*=================================*/

if exists (select 1 from sysobjects where name = 'FK_MERETOTO_TOTO' and type = 'RI')
begin
	alter table AP_TOTO drop constraint FK_MERETOTO_TOTO
	print 'Foreign key AP_TOTO.FK_MERETOTO_TOTO dropped'
end
go
if exists (select 1 from sysobjects where name = 'FK_MERETOTO2_TOTO' and type = 'RI')
begin
	alter table AP_TOTO drop constraint FK_MERETOTO2_TOTO
	print 'Foreign key AP_TOTO.FK_MERETOTO2_TOTO dropped'
end
go


/*==============================*/
/*   Creation des contraintes   */
/*==============================*/

alter table AP_TOTO add constraint FK_MERETOTO_TOTO
foreign key (PORTFOLIO_CODE, AUTOMATIC_UPDATE) references AP_MERETOTO (ISIN_CODE, AUTOMATIC_UPDATE)
go

alter table AP_TOTO add constraint FK_MERETOTO2_TOTO
foreign key (PORTFOLIO_CODE, TOTO_DATE) references AP_MERETOTO2 (ISIN_CODE, AUTOMATIC_UPDATE)
go


/*==================================*/
/*   Verification des contraintes   */
/*==================================*/

if exists (select 1 from sysobjects where name = 'FK_MERETOTO_TOTO' and type = 'RI')
	print 'Foreign key AP_TOTO.FK_MERETOTO_TOTO created'
go

if exists (select 1 from sysobjects where name = 'FK_MERETOTO2_TOTO' and type = 'RI')
	print 'Foreign key AP_TOTO.FK_MERETOTO2_TOTO created'
go