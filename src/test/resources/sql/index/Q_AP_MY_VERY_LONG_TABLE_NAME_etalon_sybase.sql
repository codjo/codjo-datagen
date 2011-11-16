/*=============================*/
/*   Suppression des indexes   */
/*=============================*/

if exists (select 1 from sysobjects o, sysindexes i
		where o.id = i.id and i.name = 'X1_Q_AP_MY_VERY_LONG_TABLE_NAM'
		and o.name = 'Q_AP_MY_VERY_LONG_TABLE_NAME' and o.type = 'U')
begin
	drop index Q_AP_MY_VERY_LONG_TABLE_NAME.X1_Q_AP_MY_VERY_LONG_TABLE_NAM
	print 'Index Q_AP_MY_VERY_LONG_TABLE_NAME.X1_Q_AP_MY_VERY_LONG_TABLE_NAM dropped'
end
go


/*==========================*/
/*   Creation des indexes   */
/*==========================*/

create  unique  clustered  index X1_Q_AP_MY_VERY_LONG_TABLE_NAM on Q_AP_MY_VERY_LONG_TABLE_NAME (ID)
go


/*==============================*/
/*   Verification des indexes   */
/*==============================*/

if exists (select 1 from sysobjects o, sysindexes i
		where o.id = i.id and i.name = 'X1_Q_AP_MY_VERY_LONG_TABLE_NAM'
		and o.name = 'Q_AP_MY_VERY_LONG_TABLE_NAME' and o.type = 'U')
	print 'Index Q_AP_MY_VERY_LONG_TABLE_NAME.X1_Q_AP_MY_VERY_LONG_TABLE_NAM created'
go
