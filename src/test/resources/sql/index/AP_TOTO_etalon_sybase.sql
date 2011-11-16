/*=============================*/
/*   Suppression des indexes   */
/*=============================*/

if exists (select 1 from sysobjects o, sysindexes i
		where o.id = i.id and i.name = 'X1_AP_TOTO'
		and o.name = 'AP_TOTO' and o.type = 'U')
begin
        drop index AP_TOTO.X1_AP_TOTO
	    print 'Index AP_TOTO.X1_AP_TOTO dropped'
end
go

if exists (select 1 from sysobjects o, sysindexes i
		where o.id = i.id and i.name = 'X2_AP_TOTO'
		and o.name = 'AP_TOTO' and o.type = 'U')
begin
	drop index AP_TOTO.X2_AP_TOTO
	print 'Index AP_TOTO.X2_AP_TOTO dropped'
end
go

if exists (select 1 from sysobjects o, sysindexes i
		where o.id = i.id and i.name = 'X3_AP_TOTO'
		and o.name = 'AP_TOTO' and o.type = 'U')
begin
	drop index AP_TOTO.X3_AP_TOTO
	print 'Index AP_TOTO.X3_AP_TOTO dropped'
end
go


/*==========================*/
/*   Creation des indexes   */
/*==========================*/

create unique clustered index X1_AP_TOTO on AP_TOTO (PORTFOLIO_CODE, DIVIDEND_DATE)
go

create unique index X2_AP_TOTO on AP_TOTO (PORTFOLIO_CODE)
go

create index X3_AP_TOTO on AP_TOTO (PORTFOLIO_CODE, AUTOMATIC_UPDATE)
go


/*==============================*/
/*   Verification des indexes   */
/*==============================*/

if exists (select 1 from sysobjects o, sysindexes i
		where o.id = i.id and i.name = 'X1_AP_TOTO'
		and o.name = 'AP_TOTO' and o.type = 'U')
	print 'Index AP_TOTO.X1_AP_TOTO created'
go

if exists (select 1 from sysobjects o, sysindexes i
		where o.id = i.id and i.name = 'X2_AP_TOTO'
		and o.name = 'AP_TOTO' and o.type = 'U')
	print 'Index AP_TOTO.X2_AP_TOTO created'
go

if exists (select 1 from sysobjects o, sysindexes i
		where o.id = i.id and i.name = 'X3_AP_TOTO'
		and o.name = 'AP_TOTO' and o.type = 'U')
	print 'Index AP_TOTO.X3_AP_TOTO created'
go