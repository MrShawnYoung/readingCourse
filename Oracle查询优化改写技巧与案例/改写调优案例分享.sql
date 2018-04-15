/*为什么不建议使用标量子查询*/
select empno,
       ename,
       sal,
       deptno,
       (select d.dname from dept d where d.deptno = e.deptno) as dname
  from emp e;

select e.empno, e.ename, e.sal, e.deptno, d.dname
  from emp e
  left join dept d
    on (d.deptno = e.deptno);

select /*+ use_nl(e,d)*/
 e.empno, e.ename, e.sal, e.deptno, d.dname
  from emp e
  left join dept d
    on (d.deptno = e.deptno);
/*用LEFT JOIN优化标量子查询*/
select s.sid,
       s.sname,
       s.shot,
       s.stype,
       (select a.aid from a where a.aid = s.aids) aid,
       (select a.aname from a where a.aid = s.aids) aname,
       (select a.atime from a where a.aid = s.aids) aatime
  from s;

select s.sid, s.sname, s.shot, s.stype, a.aid, a.aname, a.aatime
  from s
  left join a
    on (a.aid = s.aids);
/*用LEFT JOIN优化标量子查询之聚合改写*/
select d.department_id,
       d.department_name,
       d.location_id,
       nvl((select sum(e.salary)
             from hr.employees e
            where e.department_id = d.deartment_id),
           0) as sum_sal
  from hr.departments d;
--报错
select d.dearptment_id,
       d.department_name,
       d.location_id,
       nvl(sum(e.sum_sal), 0) as sum_sal
  from hr.departments d
  left join hr.employees e
    on (e.department_id = d.department_id);
--先汇总
select e.department_id, sum(e.salary) as sum_sal
  from hr.employees e
 group by e.department_id
--后关联
select d.dearptment_id,
       d.department_name,
       d.location_id,
       nvl(e.sum_sal, 0) as sum_sal
  from hr.departments d
  left join (select e.department_id, sum(e.salary) as sum_sal
               from hr.employees e
              group by e.department_id) e
    on (e.department_id = d.department_id);
/*用LEFT JOIN及行转列优化标量子查询*/
select /*省略部分返回列*/
 (select round(v, 2)
    from f2
   where f2.unim = F4101.imitim
     and m = '1'
     and f2.umum = f4101.imuom1) as m3,
 (select round(v, 2)
    from f2
   where f2.unim = F4101.imitim
     and m = '2'
     and f2.umum = f4101.imuom1) as kg,
 (select round(v, 2)
    from f2
   where f2.unim = F4101.imitim
     and m = '3'
     and f2.umum = f4101.imuom1) as kn,
 (select round(v, 2)
    from f2
   where f2.unim = F4101.imitim
     and m = '4'
     and f2.umum = f4101.imuom1) as mh,
 (select round(v, 2)
    from f2
   where f2.unim = F4101.imitim
     and m = '5'
     and f2.umum = f4101.imuom1) as ml,
 (select round(v, 2)
    from f2
   where f2.unim = F4101.imitim
     and m = '6'
     and f2.umum = f4101.imuom1) as mw,
 (select max(bpuprc) / 10000
    from f4106
   where trim(bpmcu) = 'ZX10'
     and bpcrcd = 'CNY'
     and f4106.bplitm = f4101.imlitm) bpuprc
  from f4101, f4102
 where f4101.imitm = f4102.ibitm
   and f4101.imlitm = f4102.iblitm
   and imbcu <> 'ZX10'
   and imdsc1 not like '%取消%'
   and (imsrp5 in ('1', '6') or imsrp3 in ('37', '38') or
       iblitm in ('506000040') and trim(ibmcu) = 'SF10');

select umitm,
       umum,
       max(case
             when m = '1' then
              round(v, 2)
           end) as m3,
       max(case
             when m = '2' then
              round(v, 2)
           end) as kg,
       max(case
             when m = '3' then
              round(v, 2)
           end) as kn,
       max(case
             when m = '4' then
              round(v, 2)
           end) as mh,
       max(case
             when m = '5' then
              round(v, 2)
           end) as ml,
       max(case
             when m = '6' then
              round(v, 2)
           end) as mw
  from f2
 group by umitm, umum;

select /*省略部分返回列*/
  from f4101
 inner join f4102
    on (f4101.imitm = f4102.ibitm and f4101.imlitm = f4102.iblitm)
  left join (select imitm,
                    umum,
                    max(case
                          when m = '1' then
                           round(v, 2)
                        end) as m3,
                    max(case
                          when m = '2' then
                           round(v, 2)
                        end) as kg,
                    max(case
                          when m = '3' then
                           round(v, 2)
                        end) as kn,
                    max(case
                          when m = '4' then
                           round(v, 2)
                        end) as mh,
                    max(case
                          when m = '5' then
                           round(v, 2)
                        end) as ml,
                    max(case
                          when m = '6' then
                           round(v, 2)
                        end) as mw
               from f2
              group by unitm, umum) f2
    on (f2.umitm = f4101.imitm and f2.umum = f4101.imuom1)
  left join (select bplitm, max(bpuprc) / 10000 as bpuprc
               from f4106
              where trim(bpmcu) = 'ZX10'
                and bpcrcd = 'CNY'
              group by bplitm) f4101
    on f4106.bplitm = f4101.imlitm
 where ibmcu <> 'ZX10'
   and imdsc1 not like '%取消%'
   and (imsrp5 in ('1', '6') or imsrp3 in ('37', '38') or
       iblitm in ('506000040') and trim(ibmcu) = 'SF10');
/*标量中有ROWNUM=1*/
select s.sid,
       s.sname,
       (select cid
          from b
         where b.sid = s.sid
           and b.status in ('1', '3')
           and b.sstype = '6'
           and rownum = 1) cid,
       (select cid
          from b
         where b.sid = s.sid
           and b.status in ('1', '3')
           and b.sstype = '8'
           and rownum = 1) ringcid,
       (select cid
          from b
         where b.sid = s.sid
           and b.status in ('1', '3')
           and b.sstype = '1'
           and rownum = 1) mvcid
  from s
 where 1 = 1
   and exists (select 1
          from b c wher c.sid.s.sid and status in('1', '3') and rownum = 1)
   and not exists (select 1
          from b c
         where c.sstype = '8'
           and c.price = '0'
           and c.distributionarea != '99'
           and c.distributionarea is not null
           and s.sid = c.sid);

select deptno,
       dname,
       (select ename
          from emp e
         where e.deptno = d.deptno
           and rownum = 1) as ename
  from dept d;

create index idx_emp_2 on emp(deptno,ename);
--改用max(行转列)
select c.sid,
       max(case
             when c.sstype = '1' then
              cid
           end) as mvcid,
       max(case
             when c.sstype = '8' then
              cid
           end) as ringcid,
       max(case
             when c.sstype = '6' then
              cid
           end) as cid
  from b c
 where status in ('1', '3')
 group by c.sid;

select s.sid, s.sname, c.cid, c.ringcid, c.mvcid
  from s
 inner join (select c.sid,
                    max(case
                          when c.sstype = '1' then
                           cid
                        end) as mvcid,
                    max(case
                          when c.sstype = '8' then
                           cid
                        end) as ringcid,
                    max(case
                          when c.sstype = '6' then
                           cid
                        end) as cid
               from b c
              where status in ('1', '3')
              group by c.sid) c
    on (c.sid = s.sid)
 where 1 = 1
   and not exists (select 1
          from b c
         where c.sstype = '8'
           and c.price = '0'
           and c.distributionarea != '99'
           and c.distributionarea is not null
           and s.sid = c.sid);
/*不等连接的标量子查询改写（一）*/
select a.licenceid,
       a.data_source,
       a.street,
       (select min(contdate)
          from ct
         where ct.licenceid = a.licenceid
           and ct.data_source = a.data_source
           and trunc(contdate) >= a.opensaledate) as mincontdate,
       (select min(buydate)
          from ct
         where ct.licenceid = a.licenceid
           and ct.data_source = a.data_source
           and trunc(buydate) >= a.opensaledate) as minbuydate
  from a;

select a.lincenceid,
       a.data_source,
       a.street,
       ct2.mincontdate as mincontdate, --标量2
       ct2.minbuydate  as minbuydate --标量3
  from a
  left join (select ct.licenceid,
                    ct.data_source,
                    trunc(min(contdate)) mincontdate,
                    trunc(min(buydate)) minbuydate
               from ct
              group by ct.licenceid, ct.data_source) ct2
    on ct2.licenceid = a.licenceid
   and ct2.data_source = a.data_source
   and ct2.mincontdate >= a.opensaledate
   and ct2.minbuydate >= a.opensaledate;
--最佳优化
select a.rowid as rid,
       min(case
             when trunc(ct.contdate) >= a.opensaledate then
              ct.contdate
           end) as mincontdate,
       min(case
             when trunc(ct.buydate) >= a.opensaledate then
              ct.buydate
           end) as minbuydate
  from ct
 inner join a
    on (ct.licenceid = a.licenceid and ct.data_source = a.data_source)
 group by a.rowid;

with ct2 as
 (select a.rowid as rid,
         min(case
               when trunc(ct.contdate) >= a.opensaledate then
                ct.contdate
             end) as mincontdate,
         min(case
               when trunc(ct.buydate) >= a.opensaledate then
                ct.buydate
             end) as minbuydate
    from ct
   inner join a
      on (ct.licenceid = a.licenceid and ct.data_source = a.data_source)
   group by a.rowid)
select a.licenceid,
       a.data_source,
       a.street,
       ct2.mincontdate as mincontdate, --标量2
       ct2.minbuydate  as minbuydate --标量3
  from a
  left join ct2
    on (ct2.rid = a.rowid);
/*不等连接的标量子查询改写（二）*/
select s.stkcode,
       t.mktcode,
       t.stype,
       t.sname,
       (select sum(c.hsl)
          from c
         where c.stkcode = s.stkcode
           and c.mktcode = t.mkcode
           and c.calcdate between
               to_char(to_date(s.tdate, 'yyyymmdd') - 365, 'yyyymmdd') and
               s.tdate) as f1,
       (select decode(count(c.calcdate),
                      0,
                      null,
                      sum(c.hsl) / count(c.calcdate))
          from c
         where c.stkcode = s.stkcode
           and c.mktcode = t.mktcode
           and c.calcdate between
               to_char(to_date(s.tdate, 'yyyymmdd') - 365, 'yyyymmdd') and
               s.tdate) as f2,
       s.tdate
  from s, t
 where s.stkcode = t.scode
   and t.status = 1
   and t.stype = 2
   and s.tdate >= to_number(to_char(sysdate - 3, 'YYYYMMDD'));
--最佳优化①
with t0 as
 (select rownum as sn, s.stkcode, t.mktcode, t.stype, t.sname, s.tdate
    from s, t
   where s.stkcode = t.scode
     and t.status = 1
     and t.stype = 2
     and s.tdate >= to_number(to_char(sysdate - 3, 'YYYYMMDD')))

with t0 as
 (select rownum as sn, s.stkcode, t.mktcode, t.stype, t.sname, s.tdate
    from s, t
   where s.stkcode = t.scode
     and t.status = 1
     and t.stype = 2
     and s.tdate >= to_number(to_char(sysdate - 3, 'YYYYMMDD')))
select t.stkcode, tmktcode, t.stype, t.sname, t.tdate, c.f1, c.f2
  from t0 t
  left join (select t.sn,
                    sum(c.hsl) as f1,
                    decode(count(c.calcdate),
                           0,
                           null,
                           sum(c.hsl) / count(c.calcdate)) as f2
               from c
              inner join t0 t
                 on (c.stkcode = t.stkcode and c.mktcode = t.mktcode)
              where c.calcdate between
                    to_char(to_date(t.tdate, 'yyyymmdd') - 365, 'yyyymmdd') and
                    t.tdate
              group by t.sn) c
    on (c.sn = t.sn);
--最佳优化②
with t0 as
 (select rownum as sn, s.stkcode, t.mktcode, t.stype, t.sname, s.tdate
    from s, t
   where s.stkcode = t.scode
     and t.status = 1
     and t.stype = 2
     and s.tdate >= to_number(to_char(sysdate - 3, 'YYYYMMDD')))
select t.stkcode,
       tmktcode,
       t.stype,
       t.sname,
       t.tdate,
       sum(c.hsl) as f1,
       decode(count(c.calcdate), 0, null, sum(c.hsl) / count(c.calcdate)) as f2
  from t0 t
  left join c
    on (c.stkcode = t.stkcode and c.mktcode = t.mktcode)
 where c.calcdate between
       to_char(to_date(t.tdate, 'yyyymmdd') - 365, 'yyyymmdd') and t.tdate
 group by t.sn, t.stkcode, t.mktcode, t.stype, t.sname, t.tdate;
--案例
create table a as select * from scott.emp;
create table b as select * from scott.emp;

select a.empno,
       a.ename,
       a.sal,
       (select sum(b.sal)
          from b
         where b.sal >= a.sal - 100
           and b.sal <= a.sal) as 改写前,
       c.改写后
  from a
  left join (select a.sal, sum(b.sal) as 改写后
               from b
              inner join a
                 on (b.sal >= a.sal - 100 and b.sal <= a.sal)
              group by a.sal) c
    on c.sal = a.sal
 order by 3;

select a.empno as a_empno,
       b.empno as b_empno,
       a.sal   as a_sal,
       b.sal   as b_sal
  from b
 inner join a
    on (b.sal >= a.sal - 100 and b.sal <= a.sal)
 where a.sal in (1250, 3000)
 order by 3, 1, 4;

select a.empno,
       a.ename,
       a.sal,
       (select sum(b.sal)
          from b
         where b.sal >= a.sal - 100
           and b.sal <= a.sal) as 改写前,
       c.改写后
  from a
  left join (select a.rowid as rid, sum(b.sal) as 改写后
               from b
              inner join a
                 on (b.sal >= a.sal - 100 and b.sal <= a.sal)
              group by a.rowid) c
    on c.rid = a.rowid
 order by 3;
/*标量子查询与改写逻辑的一致性*/
select *
  from (select * from t1 where col4_1 = '00') a
  left join (select col3_1,
                    col3_2 col1,
                    col2,
                    (select distinct col2_1
                       from t2
                      where col2_2 = a.col2
                        and col2_3 <= to_date('2012-09-30', 'YYYY-MM-DD')
                        and col2_4 <= to_date('2012-09-30', 'YYYY-MM-DD')) col3
               from t3 a
              where col2_3 <= to_date('2012-09-30', 'YYYY-MM-DD')
                and col2_4 > to_date('2012-09-30', 'YYYY-MM-DD')) b
    on a.col4 = b.col3_1;

select *
  from (select * from t1 where col4_1 = '00') a
  left join (select distinct col3_1, col3_2 col1, a.col2, b.col2_1 as col3
               from t3 a
               join (select *
                      from t2 b
                     where b.col2_3 <= to_date('2012-09-30', 'YYYY-MM-DD')
                       and b.col2_4 > to_date('2012-09-30', 'YYYY-MM-DD')) b
                 on a.col2 = b.col2_2
                and a.col2_3 <= to_date('2012-09-30', 'YYYY-MM-DD')
                and b.col2_4 > to_date('2012-09-30', 'YYYY-MM-DD')) b
    on a.col4 = b.col3_1;

create table dept2 as select * from dept;
insert into dept2
  select * from dept where deptno = 10;

select a.job,
       a.deptno,
       (select distinct dname from dept2 b where b.deptno = a.deptno) as dname
  from emp a
 order by 1, 2, 3;

select distinct a.job, a.deptno, b.dname
  from emp a
  left join dept2 b
    on b.deptno = a.deptno;

select a.job, a.deptno, b.dname
  from emp a
  left join (select dname, deptno from dept2 group by dname, deptno) b
    on b.deptno = a.deptno;
/*用分析函数优化标量子查询（一）*/
select a.*,
       case
         when (select count(1)
                 from ii b
                where b.id > 0
                  and b.flag = 2
                  and b.i_code = a.i_code
                  and b.c_id not in
                      (select c_id from c where ig_name like '%停用%')) > 1 then
          2
         else
          1
       end as mulinv
  from ii a
 where (a.id > 0 and itemdesc like :1 and a.isphantom <> :2)
   and a.c_id = :3
 order by a.i_code, a.i_name, a.d_id;

select count(1)
  from ii
  left join c
    on (c.c_id = ii.c_id and c.ig_name like '%停用%')
 where ii.id > 0
   and ii.flag = 2
   and c.c_id is null
   and ii.i_code = ii.i_code;

select *
  from (select ii.*,
               case
               /*用分析函数代替标量自连接*/
                 when (sum(case
                             when flag = 2 and c.c_id is null then
                              1
                           end) over(partition by ii.i_code)) > 1 then
                  2
                 else
                  1
               end as mulinv
          from ii
        /*因c.cid为主键，所以可改为LEFT JOIN，而不必担心主查询数据会翻倍*/
          left join c
            on (c.c_id = ii.c_id nad c.ig_name like '%停用%')
        /*为了保证分析函数窗口内数据与原标量范围一致，这里的过滤条件要保持一致*/
         where ii.id > 0)
/*提取出原标量所需数据后再应用其他的过滤条件*/
 where itemdesc like :1
   and ii.isphantom <> :2
   and ii.c_id = :3
 order by ii.i_code, ii.i_name, ii.d_id;
/*用分析函数优化标量子查询（二）*/
select a.code as code,
       a.m_code as m_code,
       a.stktype as f_stype,
       a.e_year as e_year,
       b.sname as sname,
       a.c_date as c_date,
       (select sum(valuef2)
          from a t
         where t.code = a.code
           and t.c_date between
               to_char(to_date(a.c_date, 'YYYYMMDD') - 180, 'YYYYMMDD') and
               a.c_date
           and t.e_year = a.e_year) f70115_70011,
       (select sum(valuef1)
          from a t
         where t.code = a.code
           and t.c_date between
               to_char(to_date(a.c_date, 'YYYYMMDD') - 180, 'YYYYMMDD') and
               a.c_date
           and t.e_year = a.e_year) f70104_70011,
       (select sum(valuef6)
          from a t
         where t.code = a.code
           and t.c_date between
               to_char(to_date(a.c_date, 'YYYYMMDD') - 180, 'YYYYMMDD') and
               a.c_date
           and t.e_year = a.e_year) f70126_70011,
       (select sum(valuef5)
          from a t
         where t.code = a.code
           and t.c_date between
               to_char(to_date(a.c_date, 'YYYYMMDD') - 180, 'YYYYMMDD') and
               a.c_date
           and t.e_year = a.e_year) f70131_70011,
       '-' as f_unit
  from a, b@link b
 where a.code = b.scode
   and b.stype = 2
   and b.status = 1
   and c_date = 20140218
   and b.scode = '000001';
--标量子查询部分
select sum(valuef5)
  from a t
 where t.code = '000001'
   and t.c_date between
       to_char(to_date('20140218', 'YYYYMMDD') - 180, 'YYYYMMDD') and
       '20140218'
   and t.e_year = a.e_year;

select a.*, b.sname as sname
  from (select a.code as code,
               a.m_code as m_code,
               a.stktype as f_stype,
               a.e_year as e_year,
               a.c_date as c_date,
               sum(valuef2) over(partition by a.e_year) as f70115_70011,
               sum(valuef1) over(partition by a.e_year) as f70104_70011,
               sum(valuef6) over(partition by a.e_year) as f70126_70011,
               sum(valuef5) over(partition by a.e_year) as f70131_70011,
               '-' as f_unit
          from a
         where (a.c_date >=
               to_char(to_date(20140218, 'YYYYMMDD') - 180, 'YYYYMMDD') and
               a.c_date <= 20140218)
           and a.code = '000001') a
 inner join (select b.sname, b.scode
               from b@link b
              where b.stype = 2
                and b.status = 1
                and b.scode = '000001') b
    on (a.code = b.scode)
 where a.c_date = 20140218;

select a.code    as code,
       a.m_code  as m_code,
       a.stktype as f_stype,
       a.e_year  as e_year,
       a.c_date  as c_date,
       a2.*,
       b.sname   as sname
  from a
 inner join (select a.e_year,
                    sum(valuef2) over(partition by a.e_year) as f70115_70011,
                    sum(valuef1) over(partition by a.e_year) as f70104_70011,
                    sum(valuef6) over(partition by a.e_year) as f70126_70011,
                    sum(valuef5) over(partition by a.e_year) as f70131_70011,
                    '-' as f_unit
               from a
              where (a.c_date >=
                    to_char(to_date(20140218, 'YYYYMMDD') - 180, 'YYYYMMDD') and
                    a.c_date <= 20140218)
                and a.code = '000001'
              group by e_year) a2
    on (a2.e_year = a.e_year)
 inner join (select b.sname, b.scode
               from b@link b
              where b.stype = 2
                and b.status = 1
                and b.scode = '000001') b
    on (a.code = b.scode)
 where a.c_date = 20140218;
/*用分析函数优化标量子查询（三）*/
select a.code as code,
       a.m_code as m_code,
       a.stktype as f_stype,
       a.e_year as e_year,
       b.sname as sname,
       a.c_date as c_date,
       to_char(sysdate, 'YYYYMMDD') as createtime,
       to_char(sysdate, 'YYYYMMDD') as updatetime,
       (select sum(valuef2)
          from a t
         where t.code = a.code
           and t.c_date between
               to_char(to_date(a.c_date, 'YYYYMMDD') - 180, 'YYYYMMDD') and
               a.c_date
           and t.e_year = a.e_year) f70115_70011,
       (select sum(valuef1)
          from a t
         where t.code = a.code
           and t.c_date between
               to_char(to_date(a.c_date, 'YYYYMMDD') - 180, 'YYYYMMDD') and
               a.c_date
           and t.e_year = a.e_year) f70104_70011,
       (select sum(valuef6)
          from a t
         where t.code = a.code
           and t.c_date between
               to_char(to_date(a.c_date, 'YYYYMMDD') - 180, 'YYYYMMDD') and
               a.c_date
           and t.e_year = a.e_year) f70126_70011,
       (select sum(valuef5)
          from a t
         where t.code = a.code
           and t.c_date between
               to_char(to_date(a.c_date, 'YYYYMMDD') - 180, 'YYYYMMDD') and
               a.c_date
           and t.e_year = a.e_year) f70131_70011,
       '-' as f_unit
  from a, b@link b
 where a.code = b.scoded
   and b.stype = 2
   and b.status = 1
   and c_date >= to_char(sysdate - 3, 'YYYYMMDD');

select a.*,
       b.sname as sname,
       to_char(sysdate, 'YYYYMMDD') as createtime,
       to_char(sysdate, 'YYYYMMDD') as updatetime,
  from (select a.code as code,
               a.m_code as m_code,
               a.stktype as f_stype,
               a.e_year as e_year,
               a.c_date as c_date,
               case
                 when a.c_date >= to_char(sysdate - 3, 'YYYYMMDD') then
                  sum(valuef2)
                  over(partition by a.code,
                       a.e_year order by to_date(c_date, 'YYYYMMDD')
                       range between 180 preceding and current row)
               end as f70115_70011,
               case
                 when a.c_date >= to_char(sysdate - 3, 'YYYYMMDD') then
                  sum(valuef1)
                  over(partition by a.code,
                       a.e_year order by to_date(c_date, 'YYYYMMDD')
                       range between 180 preceding and current row)
               end as f70104_70011,
               case
                 when a.c_date >= to_char(sysdate - 3, 'YYYYMMDD') then
                  sum(valuef6)
                  over(partition by a.code,
                       a.e_year order by to_date(c_date, 'YYYYMMDD')
                       range between 180 preceding and current row)
               end as f70126_70011,
               case
                 when a.c_date >= to_char(sysdate - 3, 'YYYYMMDD') then
                  sum(valuef5)
                  over(partition by a.code,
                       a.e_year order by to_date(c_date, 'YYYYMMDD')
                       range between 180 preceding and current row)
               end as f70131_70011,
               '-' as f_unit
          from a
         where a.c_date >= to_char(sysdate - 3 - 180, 'YYYYMMDD')) a
 inner join b@link b
    on (a.code = b.scode)
 where b.stype = 2
   and b.status = 1
   and a.c_date >= to_char(sysdate - 3, 'YYYYMMDD');
/*用分析函数优化标量子查询（四）*/
with up as
 (select max(bdsj) utime, ppc.jg
    from ppc, pp
   where ppc.pid = pp.pkid
     and ppc.tzlx in ('lb', 'dr', 'jy')
   group by ppc.jg),
ap as
 (select max(systime) systime, orgid from nps group by orgid)
select distinct ppc.jg orgid,
                (select name from qhdm where value = o.qhdm) qhdm,
                o.orgname,
                (select count(*)
                   from ppc, pp
                  where ppc.pid = pp.pkid
                    and ppc.jg = o.orgid
                    and ppc.tzlx in ('lb', 'dr')
                    and ppc.bdsj >= '2013-09-26 00:00:00'
                    and ppc.bdsj > nvl(ap.systime, '1600-09-14 00:00:00')) lb,
                (select count(*)
                   from ppc, pp, spxx
                  where ppc.pid = pp.pkid
                    and spxx.eventid = ppc.eventid
                    and spxx.xm = pp.xm
                    and spxx.sfzh = pp.sfzh
                    and upper(spxx.jybiao) <> 'LTRY'
                    and ppc.jg = o.orgid
                    and ppc.tzlx = 'jy'
                    and ppc.bdsj >= '2013-09-26 00:00:00'
                    and ppc.bdsj > nvl(ap.systime, '1600-09-14 00:00:00')) jy,
                up.utime,
                o.qfbs,
                '.' contetxt,
                (select orgname from oo where orgid = o.porgid) porgname,
                o.porgid
  from ppc
  left join pp
    on ppc.pid = pp.pkid
  left join (select * from oo where youxiao = '1') o
    on o.orgid = ppc.jg
  left join up
    on up.ppc.jg = o.orgid
  left join ap
    on ap.orgid = o.orgid
 where ppc.tzlx in ('lb', 'dr', 'jy')
   and nvl(ap.systime, '1600-09-14 00:00:00') < up.utime
   and ppc.bdsj >= '2013-09-26 00:00:00';

with up as
 (select max(bdsj) utime, ppc.jg
    from ppc, pp
   where ppc.pid = pp.pkid
     and ppc.tzlx in ('lb', 'dr', 'jy')
   group by ppc.jg),
ap as
 (select max(systime) systime, orgid from nps group by orgid),
x0 as
 (select distinct ppc.jg orgid,
                  (select name from qhdm where value = o.qhdm) qhdm,
                  o.orgname,
                  count(case
                          when ppc.bdsj > nvl(ap.systime, '1600-09-14 00:00:00') and
                               ppc.tzlx in ('lb', 'dr') and pp.pkid is not null then
                           ppc.jg
                        end) over(partition by ppc.jg) as lb,
                  count(case
                          when ppc.bdsj > nvl(ap.systime, '1600-09-14 00:00:00') and
                               ppc.tzlx = 'jy' and and upper(spxx.jybiao) <> 'LTRY' then
                           ppc.jg
                        end) over(partition by ppc.jg) as jy,
                  up.utime,
                  o.qfbs,
                  '.' context,
                  (select orgname from oo where orgid = o.porgid) porgname,
                  o.porgid,
                  ap.systime
    from ppc
   inner join (select * from oo where youxiao = '1') o
      on o.orgid = ppc.jg
   inner join up
      on up.ppc.jg = ppc.jg
    left join ap
      on ap.orgid = ppc.jg
    left join pp
      on ppc.pid = pp.pkid
    left join spxx
      on (spxx.eventid = ppc.eventid and spxx.xm = pp.xm and
         spxx.sfzh = pp.sfzh)
   where ppc.tzxl in ('lb', 'dr', 'jy')
        -- and nvl(ap.systime,'1600-09-14 00:00:00') < up.utime
     and ppc.bdsj >= '2013-09-26 00:00:00')
select * from x0 where nvl(systime, '1600-09-14 00:00:00') < utime;
/*用MERGE改写优化UPDATE*/
create table t_objects as select * from dba_objects;
create table t_tables as select * from dba_tables;

alter table t_objects add tablespace_name varchar2(30);

update t_objects o
   set o.tablespace_name =
       (select t.tablespace_name
          from t_tables t
         where t.owner = o.owner
           and t.table_name = o.object_name)
 where exists (select t.tablespace_name
          from t_tables t
         where t.owner = o.owner
           and t.table_name = o.object_name);

merge into t_objects o
using t_tables t
on (t.owner = o.owner and t.table_name = o.object_name)
when matched then
  update set o.tablespace_name = t.tablespace_name;
/*用MERGE改写有聚合操作的UPDATE（一）*/
update f3111
   set col1 =
       (select distinct col2
          from f3112
         where col3 = col4
           and nchar_col = col6)
 where trim(nchar_col) = 'CD10'
   and col7 = 'WX'
   and col8 = 'CD1999'
   and col1 <> '(select distinct col2 from f3112 where col3 = col4 and
nchar_col = col6)'
   and col9 = 0;

update f3111
   set col1 =
       (select distinct col2
          from f3112
         where col3 = col4
           and nchar_col = col6)
 where trim(nchar_col) = ' CD10'
   and col7 = 'WX'
   and col8 = 'CD1999'
   and col1 <> (select distinct col2
                  from f3112
                 where col3 = col4
                   and nchar_col = col6)
   and col9 = 0;

merge into (select a.col1, a.col4, a.nchar_col
              from f3111 a
             where a.nchar_col = ' CD10'
               and a.col7 = 'WX'
               and a.col8 = 'CD1999'
               and a.col9 = 0) a
using (select b.col2, b.col3, b.col6
         from f3112 b
        group by b.col2, b.col3, b.col6) b
on (b.col3 = a.col4 and a.nchar_col = b.col6)
when matched then
  update set a.col1 = to_char(b.col2) where a.col1 <> to_char(b.col2);

create table emp2 as select * from emp;
create index idx_emp2 on emp2(to_char(hiredate,'yyyy-mm-dd'));

select * from emp2 where to_char(hiredate, 'yyyy-mm-dd') = '1981-06-09';
select * from emp2 where to_char(hiredate, 'YYYY-MM-DD') = '1981-06-09';
/*用MERGE改写有聚合操作的UPDATE（二）*/
update g
   set col1 =
       (select to_char(wmsys.wm_concat(c.col2 || '-' || nvl(c.col3, c.col4)))
          from g a
          join b
            on a.col7 = b.col8
          join k c
            on b.id = c.col5
         where a.col9 = '10A'
           and g.col6 = a.col6)
 where g.col9 = '10A'
   and exists (select null
          from g a
          join b
            on a.col7 = b.col8
          join k c
            on b.id = c.col5
         where a.col9 = '10A'
           and g.col6 = a.col6);

merge into g
using (select a.col6,
              wmsys.wm_concat(c.col2 || '-' || nvl(c.col3, c.col4)) as col1
         from g a
         join b
           on a.col7 = b.col8
         join k c
           on b.id = c.col5
        where a.col9 = '10A'
        group by a.col6) x
on (x.col6 = g.col6)
when matched then
  update set g.col1 = x.col1 where g.col9 = '10A';
/*用MERGE改写UPDATE之多个子查询（一）*/
update a
   set (a.op_d_id, a.op_work_no, a.a_date, a.c_date) =
       (select d_id, a_person, a_date, c_date
          from x1
         where x1.r_number = a.r_number),
       a.s_date =
       (select s_date
          from x2
         where x2.u_id = a.u_id
           and x2.fee_date = :b4
           and rownum = 1)
 where city_code = :b3
   and mod(a.u_id, :b2) = :b1 - 1;

merge into a
using (select a.rowid as rid,
              x1.d_id,
              x1.a_person,
              x1.a_date,
              x1.c_date,
              x2.s_date
         from a
         left join x1
           on (x1.r_number = a.r_number)
         left join (select max(s_date) as s_date, x2.u_id
                     from x2
                    where x2.fee_date = :b4
                    group by x2.u_id) x2
           on (x2.u_id = a.u_id)
        where a.city_code = :b3
          and mod(a.u_id, :b2) = :b1 - 1) b
on (b.rid = a.rowid)
when matched then
  update
     set a.op_d_id    = b.d_id,
         a.op_work_no = b.a_person,
         a.a_date     = b.a_date,
         a.c_date     = b.c_date,
         a.s_date     = b.s_date;
/*用MERGE改写UPDATE之多个子查询（二）*/
update table1 f
   set f.累计金额1 =
       (select nvl(sum(nvl(b.金额1, 0)), 0)
          from table1 b
         where b.会计期间 <= f.会计期间
           and b.公司 = f.公司
           and b.部门 = f.部门
           and b.业务 = f.业务
           and b.currency_id = f.currency_id
           and substr(b.会计期间, 1, 4) = substr(f.会计期间, 1, 4)),
       f.金额2    =
       (select nvl(sum(nvl(e.金额1, 0)), 0)
          from table2 e
         where e.会计期间 = f.会计期间
           and e.公司 = f.公司
           and e.部门 = f.部门
           and e.业务 = f.业务),
       f.累计金额2 =
       (select nvl(sum(nvl(e.金额1, 0)), 0)
          from table2 e
         where e.会计期间 <= f.会计期间
           and substr(b.会计期间, 1, 4) = substr(f.会计期间, 1, 4)
           and e.公司 = f.公司
           and e.部门 = f.部门
           and e.业务 = f.业务)
 where substr(f.会计期间, 1, 4) = extract(year from sysdate);

select b.rowid as rid,
       sum(b.金额1) over(partition by b.公司, b.部门, b.业务, b.currency_id order by b.会计期间) as 累计金额1
  from table1 b
 where substr(b.会计期间, 1, 4) = extract(year form sysdate);

select e.公司, e.部门, e.业务, e.会计期间, sum(金额1) as 金额2
  from table2 e
 where substr(e.会计期间, 1, 4) = extract(year from sysdate)
 group by e.公司, e.部门, e.业务, e.会计期间;

select e.公司,
       e.部门,
       e.业务,
       e.会计期间,
       e.金额2,
       sum(金额2) over(partition by e.公司, e.部门, e.业务 order by e.会计期间) as 累计金额2
  from (select e.公司, e.部门, e.业务, e.会计期间, sum(金额1) as 金额2
          from table2 e
         where substr(e.会计期间, 1, 4) = extract(year from sysdate)
         group by e.公司, e.部门, e.业务, e.会计期间);

merge into table1 f
using (select b.rowid as rid,
              sum(b.金额1) over(partition by b.公司, b.部门, b.业务, b.currency_id order by b.会计期间) as 累计金额1 e.金额2,
              e.累计金额2
         from table1 b
         left join (select e.公司,
                          e.部门,
                          e.业务,
                          e.会计期间,
                          e.金额2,
                          sum(金额2) over(partition by e.公司, e.部门, e.业务 order by e.会计期间) as 累计金额2
                     from (select e.公司,
                                  e.部门,
                                  e.业务,
                                  e.会计期间,
                                  sum(金额1) as 金额2
                             from table2 e
                            where substr(e.会计期间, 1, 4) =
                                  extract(year from sysdate)
                            group by e.公司, e.部门, e.业务, e.会计期间) e) e
           on (e.会计期间 b.会计期间 and e.公司 = b.公司 and e.部门 b.部门 and e.业务 b.部门)
        where substr(b.会计期间, 1, 4) = extract(year form sysdate)) b
on (f.rowid = b.rid)
when matched then
  update f.累计金额1 = nvl(b.累计金额1, 0),
         f.金额2     = nvl(b.金额1, 0),
         f.累计金额2 = nvl(b.累计金额2, 0);
/*UPDATE改写为MERGE时遇到的问题*/
update mwm
   set mwm.qty1 = nvl((select sum(nvl(mws.qty, 0))
                        from mws mws
                       where mws.oid = mwm.oid
                         and mws.wid = mwm.wid nad
                       mws.seq <= mwm.out_seq),
                      0);

merge into mwm
using (select sum(qty) over(partition by wid, oid order by seq) as qty,
              wid,
              oid,
              seq
         from mws) mws
on (mws.oid(+) = mwm.oid and mws.wid(+) = mwm.wid and mws.seq(+) = mwm.out_seq)
when matched then
  update set mwm.qty1 = nvl(mws.qty, 0);

create table emp1 as select * from scott.emp where deptno = 10;
create table emp2 as select * from scott.emp where deptno = 10;

update emp1
   set emp1.sal = nvl((select sum(nvl(emp2.sal, 0))
                        from emp2
                       where emp2.empno <= emp1.empno),
                      0);

select empno, sal from emp1 order by 1;

select a.empno, a.sal, b.sum_sal
  from emp1 a
  left join (select b.empno,
                    sum(sal) over(partition by b.deptno order by b.empno) as sum_sal
               from emp2 b) b
    on (b.empno = a.empno)
 order by 1;

update emp2 set emp2.empno = emp2.empno - 1;

merge into mwm
using (seledt mwm.rowid as rid, sum(mws.qty) as qty from mwm left join mws
on (mws.oid = mwm.oid and mws.wid = mwm.wid and mws.seq <= mwm.out_seq) group by mwm.rowid) mws
on (mws.rid = mwm.rowid)
when matched then
  update set mwm.qty1 = nvl(mws.qty, 0);
/*整理优化分页语句*/
select *
  from (select a.*, rownum num
          from (select *
                  from b
                 where 1 = 1
                   and type = '10'
                   and s_cd = '1000'
                   and name like '%xxx%'
                 order by (select nvl(to_number(replace(translate(des,
                                                                  replace(translate(des,
                                                                                    '0123456789',
                                                                                    '##########'),
                                                                          '#',
                                                                          ''),
                                                                  rpad('#',
                                                                       20,
                                                                       '#')),
                                                        '#',
                                                        '')),
                                      '0')
                             from b_price b
                            where max_price = '1'
                              and b.id = b.id),
                          name) a)
 where num > 1
   and num <= 20;

select *
  from (select a.*, rownum num
          from (select a.*
                  from b a
                 inner join b_price b
                    on (b.id = a.id)
                 where 1 = 1
                   and b.max_price = '1'
                   and a.type = '10'
                   and a.s_cd = '1000'
                   and a.name like '%xxx%'
                 order by regexp_replace(des, '[^0-9]', '')) a
         where num <= 20)
 where num > 1;
/*让分页语句走正确的PLAN*/
select *
  from (select rownum r, t.*
          from (select /*+ use_hash(t,r)*/
                 t.rollno, t.bizfileno, t.id regiid, s.status
                  from t
                  left join s
                    on s.regiid = t.id
                 inner join r
                    on r.docid = t.id
                   and r.status = 3701
                 where t.rollno like 'S10%'
                   and t.status not in (-1, -2, -3, -4, 1001, 1301, 1005)
                 order by t.rollno desc) t
         where rownum <= 20)
 where r > 10;

select *
  from (select rownum r, t.*
          from (select /*+ use_nl(t,s) use_nl(t,r) leading(t,r,s)*/
                 t.rollno, t.bizfileno, t.id regiid, s.status
                  from t
                  left join s
                    on s.regiid = t.id
                 inner join r
                    on r.docid = t.id
                   and r.status = 3701
                 where t.rollno like 'S10%'
                   and t.status not in (-1, -2, -3, -4, 1001, 1301, 1005)
                 order by t.rollno desc) t
         where rownum <= 20)
 where r > 10;
/*去掉分页查询中的DISTINCT*/
create table t_objects as
select t2.user_id, t1.*
  from dba_objects t1
 inner join all_users t2
    on t2.username = t1.owner;

create table t_colums as
select b.object_id,
       tc.owner,
       tc.table_name,
       tc.column_name,
       tc.data_type,
       tc.data_type_mod,
       tc.data_type_owner,
       tc.data_length
  from all_tab_columns tc
 inner join t_objects b
    on (b.owner = tc.owner and b.ojbect_name = tc.table_name);

create table t_tables as select * from dba_tables;

create table t_users as select * from all_users;

create index idx_t_tables on t_tables(owner,table_name);
create index idx_t_columns on t_columns(object_id,owner,table_name);
create index idx_t_users on t_users(user_id,usename);
create index idx_t_objects on t_objects(created desc,user_id,object_id);

select *
  from (select a.*, rownum rn
          from (select distinct o.object_id,
                                o.owner,
                                o.object_name,
                                o.object_type,
                                o.created,
                                o.status
                  from t_objects o
                 inner join t_columns tc
                    on (tc.object_id = o.object_id)
                 inner join t_tables t
                    on (t.owner = tc.owner and t.table_name = tc.table_name)
                  left join t_users tu
                    on (tu.user_id = o.user_id)
                 where tu.username in ('HR', 'SCOTT', 'OE', 'SYS')
                 order by o.created desc) a
         where rownum <= 5)
 where rn > 0;

select * from table(dbms_xplan.display_cursor(null, 0, 'iostats'));

select *
  from (select a.*, rownum rn
          from (select /*+ index(o,idx_t_objects) leading(o)*/
                 o.object_id,
                 o.owner,
                 o.object_name,
                 o.object_type,
                 o.created,
                 o.status
                  from t_objects o
                 where exists
                 (select /*+ nl_sj qb_name(@inner)*/
                         null
                          from t_users tu
                         where (tu.user_id = o.user_Id)
                           and tu.username in ('HR', 'SCOTT', 'OE', 'SYS'))
                   and exists (select /*+ nl_sj use_nl(tc,t)*/
                         null
                          from t_columns tc
                         inner join t_tables t
                            on (t.owner = tc.owner and
                               t.table_name = tc.table_name)
                         where tc.object_id = o.object_id)
                 order by o.created desc) a
         where rownum <= 5) b
 where rn > 0;
/*用WITH语句减少自关联*/
select /*省略返回值列表*/
  from (select /*省略返回值列表*/
          from tsl ts10_
         where 1 = 1
           and ts10_.created_date >=
               to_date('2013-09-01 00:00:00', 'yyyy-MM-dd hh24:mi:ss')
           and ts10_.created_date <=
               to_date('2013-09-30 23:59:59', 'yyyy-MM-dd hh24:mi:ss')
           and ((ts10_.adjust_status = '1' and ts10_.serialstatus = '1') or
               (ts10_.adjust_status = '6' and ts10_.serialstatus = '0') or
               (ts10_.adjust_status = '2' and ts10_.serialstatus = '2'))
           and ((ts10_.sales_offi_id = ts10_.shipments_offi_id) or
               (ts10_.shipments_offi_id = 1337))
           and not exists
         (select 1
                  from tsl l
                 where l.s_no = ts10_.s_no
                   and l.serialstatus = '0'
                   and l.created_date <
                       to_date('2013-09-01 00:00:00', 'yyyy-MM-dd hh24:mi:ss'))
           and ts10_.tf_adjustment = '0') a
  left join (select s_no,
                    retailer_code,
                    retailer_name,
                    creator_name,
                    empl_type,
                    sales_offi_id,
                    sales_offi_name,
                    sales_group_name,
                    created_date
               from tsl l
              where l.s_no in
                    (select tsl1_.s_no
                       from tsl tsl1_
                      where 1 = 1
                        and tsl1_.created_date >=
                            to_date('2013-09-01 00:00:00',
                                    'yyyy-MM-dd hh24:mi:ss')
                        and tsl1_.created_date <=
                            to_date('2013-09-30 23:59:59',
                                    'yyyy-MM-dd hh24:mi:ss')
                        and ((tsl1.adjust_status = '1' nad
                             tsl1_.serialstatus = '1') or
                            (tsl1_.adjust_status = '6' and
                            tsl1_.serialstatus = '0') or
                            (tsl1_.adjust_status = '2' and
                            tsl1_.serialstatus = '2'))
                        and ((tsl1_.sales_offi_id = tsl1_.shipments_offi_id) or
                            (tsl1_.shipments_offi_id = 1337))
                        and not exists
                      (select 1
                               from tsl l
                              where l.s_no = tsl1_.s_no
                                and l.serialstatus = '0'
                                and l.created_date <
                                    to_date('2013-09-01 00:00:00',
                                            'yyyy-MM-dd hh24:mi:ss')))
                and l.serialstatus = '0') b
    on a.s_no = b.s_no
 where a.sales_offi_id = b.sales_offi_id
   and a.empl_type = '2'
 order by a.created_date, a.id desc;

with base as
 (select /*省略返回值列表*/
    from tsl tsl1_
   where 1 = 1
     and tsl1_.created_date >=
         to_date('2013-09-01 00:00:00', 'yyyy-MM-dd hh24:mi:ss')
     and tsl1_.created_date <=
         to_date('2013-09-30 23:59:59', 'yyyy-MM-dd hh24:mi:ss')
     and ((tsl1.adjust_status = '1' nad tsl1_.serialstatus = '1') or
         (tsl1_.adjust_status = '6' and tsl1_.serialstatus = '0') or
         (tsl1_.adjust_status = '2' and tsl1_.serialstatus = '2'))
     and ((tsl1_.sales_offi_id = tsl1_.shipments_offi_id) or
         (tsl1_.shipments_offi_id = 1337))
     and not exists
   (select 1
            from tsl l
           where l.s_no = tsl1_.s_no
             and l.serialstatus = '0'
             and l.created_date <
                 to_date('2013-09-01 00:00:00', 'yyyy-MM-dd hh24:mi:ss')))
select /*省略返回值列表*/
  from (select *
          from base
         where base.tf_adjustment = '0'
           and base.empl_type = '2') a
 inner join (select s_no,
                    retailer_code,
                    retailer_name,
                    creator_name,
                    empl_type,
                    sales_offi_id,
                    sales_offi_name,
                    sales_group_name,
                    created_date,
               from tsl l
              where l.s_no in (select base.s_no from base)
                and l.serialstatus = '0') b
    on (a.s_no = b.s_no and a.sales_offi_id = b.sales_offi_id)
 order by a.created_date, a.id desc;
/*用WITH改写优化查询*/
select l_target.*, rownum as rn
  from (select l0.l0_id as l0Id,
               l0.date1 as createDate,
               l0.id1 as applyId,
               bai.stitle as applyTitle,
               bai.amt1 as applyAmt,
               bai.rate1 as applyRatio,
               l0.value1 as oriPri,
               l0.value2 as leftAmt,
               sum(case
                     when lpi.status != '1' then
                      1
                     else
                      0
                   end) as remPeriods,
               l0.flag1 as flag2,
               curi.s_cnname as name2,
               curi.i_user_id as sellUserId
          from l0
          left join lpp
            on lpp.l0_id = l0.l0_id
          left join lpi0 lpi
            on lpi.id3 = lpp.id3
          left join b_apply_info bai
            on bai.i_id1 = l0.id1
          left join pbp0 pbp0
            on pbp0.id1 = l0.id1
          left join pp0 pp0
            on pbp0.id2 = pp0.id2
         inner join c_user_regist_info curi
            on l0.creditor_user_id = curi.i_user_id
         where 1 = 1
           and l0.creditor_user_id = 5263
           and l0.status != '2'
           and l0.flag1 = '1'
           and pp0.product_type = 1
         group by l0.l0_id,
                  l0.value1,
                  l0.value2,
                  l0.id1,
                  l0.date1,
                  bai.stitle,
                  bai.amt1,
                  bai.rate1,
                  curi.s_cnname,
                  curi.i_user_id) l_target
 inner join (select l2.l0_id,
                    l2.creditor_user_id,
                    lpi0.PERIOD_NUM,
                    lpi0.period_end_time
               from l0 l2
               left join lpp0
                 on l2.l0_id = lpp0.l0_id
               left join lpi0 lpi0
                 on lpp0.id3 = lpi0.id3
               left join arda0
                 on arda0.id3 = lpi0.id3
              where l2.creditor_user_id = 5263
                and l2.status != '2'
                and lpi0.period_start_time <= sysdate
                and lpi0.period_end_time > sysdate
              group by l2.l0_id,
                       lpi0.PERIOD_NUM,
                       l2.creditor_user_id,
                       lpi0.period_end_time
             having sum(arda0.REPAYMENT_AMT) = 0 or sum(arda0.REPAYMENT_AMT) is null) l_cur
    on l_cur.l0_id = l_target.l0Id
  left join (select lpp0.l0_id, lpp0.status, lpp0.PERIOD
               from lpp0
              inner join (select l2.l0_id,
                                l2.creditor_user_id,
                                lpi0.PREIOD_NUM,
                                lpi0.period_end_time
                           from l0 l2
                           left join lpp0
                             on l2.l0_id = lpp0.l0_id
                           left join lpi0 lpi0
                             on lpp0.id3 = lpi0.id3
                           left join arda0
                             on arda0.id3 = lpi0.id3
                          where l2.creditor_user_id = 5263
                            and l2.status != '2'
                            and lpi0.period_start_time <= sysdate
                            and lpi0.period_end_time > sysdate
                          group by l2.l0_id,
                                   lpi0.PERIOD_NUM,
                                   l2.creditor_user_id,
                                   lpi0.period_end_time
                         having sum(arda0.REPAYMENT_AMT) = 0 or sum(arda0.REPAYMENT_AMT) is null) lc
                 on lc.l0_id = lpp0.l0_id
              where lc.PERIOD_NUM = lpp0.PERIOD + 1) l_pre
    on l_pre.l0.id = l_target.l0Id
  left join (select ttla.SELLER_l0_ID,
                    count(*) t_cnt,
                    max(transferable) transferable
               from tla_transfer_l0_apply ttla
              where 1 = 1
                and ttla.SELLER_ID = 5263
              group by ttla.SELLER_l0_ID) l_tran
    on l_tran.SELLER_l0_ID = l_target.l0Id
  left join (select * from tla_transfer_l0_apply where transferable = '1') ttla1
    on ttla1.seller_l0_id = l_target.l0Id
 where 1 = 1
   and (l_tran.transferable is null or l_tran.transferable != '1')
   and applyRatio = 12
 order by leftAmt desc;

with l_cur as
 (select l2.l0_id,
         l2.creditor_user_id,
         lpi0.period_num,
         lpi0.period_end_time
    from l0 l2
    left join lpp0
      on l2.l0_id = lpp0.l0_id
    left join lpi0 lpi0
      on lpp0.id3 = lpi0.id3
    left join arda0
      on arda0.id3 = lpi0.id3
   where l2.creditor_user_id = 5263
     and l2.status != '2'
     and lpi0.period_start_time <= sysdate
     and lpi0.period_end_time > sysdate
   group by l2.l0_id,
            lpi0.period_num,
            l2.creditor_user_id,
            lpi0.period_end_time
  having sum(arda0.repayment_amt) = 0 or sum(arda0.repayment_amt) is null)
select l_target.*, rownum as rn
  from (select l0.l0_id as l0id,
               l0.date1 as createdate,
               l0.id1 as applyid,
               bai.stitle as applytitle,
               bai.amt1 as applyamt,
               bai.rate1 as applyratio,
               l0.value1 as oripri,
               l0.value2 as leftamt,
               sum(case
                     when lpi.status != '1' then
                      1
                     else
                      0
                   end) as remperiods,
               l0.flag1 as flag2,
               curi.s_cnname as name2,
               curi.i_user_id as selluserid
          from l0
          left join lpp
            on lpp.l0_id = l0.l0_id
          left join lpi0 lpi
            on lpi.id3 = lpp.id3
          left join b_apply_info bai
            on bai.i_id1 = l0.id1
          left join pbp0 pbp0
            on pbp0.id1 = l0.id1
          left join pp0 pp0
            on pbp0.id2 = pp0.id2
         inner join c_user_regist_info curi
            on l0.creditor_user_id = curi.i_user_id
         where 1 = 1
           and l0.creditor_user_id = 5263
           and l0.status != '2'
           and l0.flag1 = '1'
           and pp0.product_type = 1
         group by l0.l0_id,
                  l0.value1,
                  l0.value2,
                  l0.id1,
                  l0.date1,
                  bai.stitle,
                  bai.amt1,
                  bai.rate1,
                  curi.s_cnname,
                  curi.i_user_id) l_target
 inner join l_cur
    on l_cur.l0_id = l_target.l0id
  left join (select lpp0.l0_id, lpp0.status, lpp0.period
               from lpp0
              inner join l_cur lc
                 on lc.l0_id = lpp0.l0id
              where lc.period_num = lpp0.period + 1) l_pre
    on l_pre.l0_id = l_target.l0id
  left join (select ttla.seller_l0_id,
                    count(*) t_cnt,
                    max(transferable) transferable
               from tla_transfer_l0_apply ttla
              where 1 = 1
                and ttla.seller_id = 5263
              group by ttla.seller_l0_id) l_tran
    on l_tran.seller_l0_id = l_target.l0id
  left join (select * from tla_transfer_l0_apply where transferable = '1') ttla1
    on ttla1.seller_l0_id = l_target.l0id
 where 1 = 1
   and (l_tran.transferable is null or l_tran.transferable != '1')
   and applyratio = 12
 order by leftamt desc;
/*用WITH把OR改为UNION*/
with d1 as
 (select d1.c2 as c1
    from (select distinct nvl(d1.c1, 0) as c1, d1.c2 as c2
            from (select sum(case
                               when t49296.t49296_id in (1, 31, 40, 41) then
                                t49296.amount * -1
                               else
                                0
                             end) as c1,
                         t49495.segment1 as c2
                    from t49221 t49221,
                         t49495 t49495,
                         t48941 t48941,
                         (select /*这里有160多列*/
                            from t49296) t49296
                   where (t48941.c_d_id = t49296.b_date_d and
                         t48941.c_y_id = '2013' and t49221.o_id = t49296.o_id and
                         t49296.i_item_id = t49495.i_item_id and
                         t49296.o_id = t49495.o_id and
                         (t49221.attribute1 in
                         ('0', '01', '02', '03', '04', '05', '06')) and
                         t49221.name <> 'xxxxx')
                   group by t49495.segment1
                  having 0 < nvl(sum(case
                    when t49296.t49296_id in
                         (1, 31, 40, 41) then
                     t49296.amount * -1
                    else
                     0
                  end), 0)) d1) d1),
sawith0 as
 (select sum(t69824.amount_r) as c1
    from (select case
                   when (sysdate - sign_date) is null then
                    0
                   else
                    (sysdate - sign_date) / 365
                 end as fundation_date,
          /*这里有160多列*/
            from edw_cux_inn_info_header
           where current_flag = 'Y') t49157,
         t49495 t49495,
         t99532 t99532,
         t69824 t69824
   where (t49157.org_id = t69824.org_id and t49495.i_item_id = t69824.
          item_id and t49495.o_id = t69824.org_id and
          t49157.inn_s_name = 'xxxxx' and t69824.t_l_code = 'ZZZZ' and
          t69824.p_flg = 'N' and t69824.receiving_mon = t99532.cal_month_name and
          t99532.c_y_id = '2013' and substr(t49157.inn_code, 1, 1) <> 'H' and
          (t69824.approved_flag in ('N', 'R', 'Y')) and
          (t69824.cancel_flag in ('N') or t69824.cancel_flag is null)
         /*网友要求把这个or改为union*/
          and (t69824.ship_to_base_flag in (1) or
          t49495.segment1 in (select distinct d1.c1 as c1 from d1)) and
          substr(t49157.inn_code, 1, 1) <> 'T'))
select distinct d1.c1 / 10000 as c1, 'YYYY' as c2
  from sawith0 d1
 order by c1;

with d1 as
 (select d1.c2 as c1
    from (select distinct d1.c1 as c1, d1.c2 as c2
            from (select sum(case
                               when t49296.t49296_id in (1, 31, 40, 41) then
                                t49296.amount * -1
                               else
                                0
                             end) as c1,
                         t49495.segment1 as c2
                    from t49221 t49221,
                         t49495 t49495,
                         t48941 t48941,
                         (select /*这里有160多列*/
                            from t49296) t49296
                   where (t48941.c_d_id = t49296.b_date_d and
                         t48941.c_y_id = '2013' and t49221.o_id = t49296.o_id and
                         t49296.i_item_id = t49495.i_item_id and
                         t49296.o_id = t49495.o_id and
                         (t49221.attribute1 in
                         ('0', '01', '02', '03', '04', '05', '06')) and
                         t49221.name <> 'xxxxx')
                   group by t49495.segment1
                  having 0 < sum(case
                    when t49296.t49296_id in (1, 31, 40, 41) then
                     t49296.amount * -1
                    else
                     0
                  end)) d1) d1),
sawith0 as
 (select /*sum(t69824.amount_r) as c1,*/
   rownum as sn, t69824.amount_r, t69824.ship_to_base_flag, t49495.segment1
    from (select case
                   when (sysdate - sign_date) is null then
                    0
                   else
                    (sysdate - sign_date) / 365
                 end as fundation_date,
          /*这里有50多列*/
            from edw_cux_inn_info_header
           where current_flag = 'Y') t49157,
         t49495 t49495,
         t99532 t99532,
         t69824 t69824
   where (t49157.org_id = t69824.org_id and t49495.i_item_id = t69824.
          item_id and t49495.o_id = t69824.org_id and
          t49157.inn_s_name = 'xxxxx' and t69824.t_l_code = 'ZZZZ' and
          t69824.p_flg = 'N' and t69824.receiving_mon = t99532.cal_month_name and
          t99532.c_y_id = '2013' and substr(t49157.inn_code, 1, 1) <> 'H' and
          (t69824.approved_flag in ('N', 'R', 'Y')) and
          (t69824.cancel_flag in ('N') or t69824.cancel_flag is null)
         /*and (t69824.ship_to_base_flag in (1) or t49495.segment1 in (select distinct d1.c1 as c1 from d1))*/
          substr(t49157.inn_code, 1, 1) <> 'T')),
sawith1 as
 (select sum(amount_r) as c1
    from (
          /*注意这里显示的列要全，能唯一标识各行，能有PK列最好，否则union后会丢数据*/
          select amount_r, sn
            from sawith0
           where ship_to_base_flag in (1)
          union
          select amount_r, sn
            from sawith0
           where segment1 in (select distinct d1.c1 as c1 from d1 d1)))
select distinct d1.c1 / 10000 as c1, 'YYYY' as c2
  from sawith1 d1
 order by c1;

select *
  from sawith0
 where (t69824.ship_to_base_flag in (1) or
       t49495.segment1 in (select distinct d1.c1 as c1 from d1));
/*错误的WITH改写*/
select b.scode f_scode,
       b.sname f_sname,
       b.stype f_stype,
       b.mktcode f_mktcode,
       '' f_unit,
       0 f_type,
       a.f_tradeday,
       a.f20141_20015 f20183_20023,
       d.startday,
       d.endday,
       ''
  from (select a.scode, a.f_tradeday, b.tradedate f20141_20015
          from (select a.scode, c.year f_tradeday, min(a.lowprice) ndata, ''
                  from a, c
                 where a.tradedate = c.tdate
                   and a.mktcode in (1, 2)
                   and c.year = to_char(sysdate, 'YYYY')
                 group by a.scode, c.year) a,
               (select a.scode,
                       a.tradedate,
                       c.year      f_tradeday,
                       a.lowprice  ndata
                  from a, c
                 where a.tradedate = c.tdate
                   and a.mktcode in (1, 2)
                   and c.year = to_char(sysdate, 'YYYY')) b
         where a.scode = b.scode
           and a.f_tradeday = b.f_tradeday
           and a.ndata = b.ndata) a,
       sdc.security b,
       (select c.year, min(c.tdate) startday, max(c.tdate) endday, ''
          from c
         group by c.year) d
 where a.scode = b.scode
   and a.f_tradeday = d.year
   and b.stype in (2, 3);

with temp as
 (select a.scode, substr(a.t_date, 1, 4) f_t_day, min(a.lowprice) mdata, ''
    from a
   where a.mktcode in (1, 2)
     and substr(a.t_date, 1, 4) = to_char(sysdate, 'YYYY')
   group by a.scode, substr(a.t_date, 1, 4))
select b.scode f_scode,
       b.sname f_sname,
       b.stype f_stype,
       b.mktcode f_mktcode,
       '' f_unit,
       0 f_type,
       a.f_t_day,
       a.t_date,
       d.startday,
       d.endday,
       ''
  from (select a.scode, d.f_t_day, a.t_date
          from a, temp d
         where substr(a.t_date, 1, 4) = d.f_t_day
           and a.mktcode in (1, 2)
           and a.lowprice = d.ndata) a,
       sdc.security b,
       (select c.year, min(c.tdate) startday, max(c.tdate) endday, ''
          from c
         group by c.year) d
 where a.scode = b.scode
   and a.f_t_day = d.year
   and b.stype in (2, 3);

select b.scode, b.f_t_day, b.t_date as t_date
  from (select a.scode,
               a.t_date,
               c.year f_t_day,
               min(a.lowprice) over(partition by a.scode, c.year) as min_ndata,
               a.lowprice ndata
          from a, c
         where a.t_date = c.tdate
           and a.mktcode in (1, 2)
           and c.year = to_char(sysdate, 'YYYY')) b
 where min_ndata = ndata;
/*错误的分析函数用法*/
select a.deptno, a.empno, a.ename, a.sal, b.min_sal, b.max_sal
  from emp a
 inner join (select deptno, max(sal) as max_sal, min(sal) as min_sal
               from emp
              group by deptno) b
    on b.deptno = a.deptno
 where a.hiredate >= to_date('1981-01-01', 'yyyy-mm-dd')
   and a.hiredate < to_date('1982-01-01', 'yyyy-mm-dd')
   and a.deptno in (10, 20)
 order by 1, 4;

select a.deptno,
       a.empno,
       a.ename,
       a.sal,
       min(a.sal) over(partition by a.deptno) as min_sal,
       max(a.sal) over(partition by a.deptno) as max_sal
  from emp a
 where a.hiredate >= to_date('1981-01-01', 'yyyy-mm-dd')
   and a.hiredate < to_date('1982-01-01', 'yyyy-mm-dd')
   and a.deptno in (10, 20)
 order by 1, 4;

select deptno, empno, ename, sal, min_sal, max_sal
  from (select a.deptno,
               a.empno,
               a.ename,
               a.sal,
               a.hiredate,
               min(a.sal) over(partition by a.deptno) as min_sal,
               max(a.sal) over(partition by a.deptno) as max_sal
          from emp a
         where a.deptno in (10, 20)) a
 where a.hiredate >= to_date('1981-01-01', 'yyyy-mm-dd')
   and a.hiredate < to_date('1982-01-01', 'yyyy-mm-dd')
 order by 1, 4;
/*用LEFT JOIN优化多个子查询（一）*/
select a.*, b.rkids
  from (select gys.khbh,
               gys.khmc,
               wz.wzzbm,
               wz.wzmbm,
               wz.wzmc,
               wz.wzgg,
               a.sl,
               wz.jldw,
               wz.wzflmbm,
               z.sl * wz.wzflmbm jhje
          from wz@dblink wz,
               gys@dblink gys,
               (select m.khbh,
                       d.wzsbm,
                       sum(case m.rkzt
                             when '2' then
                              d.xysl
                             else
                              -1 * d.xysl
                           end) sl
                  from m@dblink m, d@dblink d
                 where m.rkid = d.rkid
                   and m.rkzt in (2, 3)
                   and m.ssny < '201311'
                 group by m.khbh, d.wzzbm) a
         where a.sl > 0
           and gys.khbh = a.khbh
           and wz.wzzbm = z.wzzbm) a,
       (select m.khbh, d.wzzbm, wmsys.wm_concat(m.kid) rkids
          from m@dblink m, d@dblink d
         where m.rkid = d.rkid
           and m.rkzt = 2
           and m.ssny < '201311'
           and m.zxid is null
           and (not exists (select 1
                              from m@dblink m1, d@dblink d1
                             where m1.rkid = d1.rkid
                               and m1.zxid = m.rkid
                               and d1.wzzdm = d.wzzbm
                               and m1.rkzt = 3) or
                (select sum(d1.xysl)
                              from m@dblink m1, d@dblink d1
                             where m1.rkid = d1.rkid
                               and m1.zxdid = m.rkid
                               and d1.wzzdm = d.wzzbm
                               and m1.rkzt = 3) < d.xysl)
         group by m.khbh, d.wzzbm) b
 where a.khbh = b.khbh(+)
   and a.wzzbm = b.wzzbm(+);

select gys.khbh,
       gys.khmc,
       wz.wzzbm,
       wz.wzmbm,
       wz.wzmc,
       wz.wzgg,
       a.sl,
       wz.jldw,
       wz.wzflmbm,
       z.sl * wz.wzflmbm jhje a.rkids
  from wz@dblink wz,
       gys@dblink gys,
       (select m.khbh,
               d.wzsbm,
               sum(case m.rkzt
                     when '2' then
                      d.xysl
                     else
                      -1 * d.xysl
                   end) sl,
               wmsys.wm_concat(case
                                 when (m, rkzt = 2 and
                                       (m1.zsdid is null or nvl(m1.xysl, 0) < d.xysl)) then
                                  m.rkid
                               end) as rkids
          from m@dblink m
         inner join d@dblink d
            on (m.rkid = d.rkid)
          left join (select sum(d1.xysl) as xysl, m1.zxdid, d1.wzzdm
                      from m@dblink m1, d@dblink d1
                     where m1.rkid = d1.rkid
                       and m1.rkzt = 3
                     group by m1.zxdid, d1.wzzbm) m1
            on (m1.zxdid = m.rkid nad m1.wzzbm = d.wzzbm)
         where m.rkzt in (2, 3)
           and m.ssny < '201311'
         group by m.khbh, d.wzzdm) a
 where a.sl > 0
   and gys.khbh = a.khbh
   and wz.wzzbm = a.wzzbm;
/*用LEFT JOIN优化多个子查询（二）*/
select count(1) num
  from (select t1.*
          from t_asset t1
         where 1 = 1
           and t1.type = 0
           and (t1.status in (1, 10, 11, 12, 100) or
               (exists (select b.resource_id
                           from t_asset_file b
                          where t1.resource_id = b.asset_code
                            and t1.status in (3, 4, 8)
                            and b.status in (1, 10, 11, 12))))
           and (exists (select 1
                          from t_asset_file a1
                         where t1.resource_id = a1.asset_code
                           and (a1.content_status = 1 or a1.content_status = 4)) or
                not exists (select 1
                              from t_asset_file a1
                             where t1.resource_id = a1.asset_code))
         order by t1.create_time desc, t1.resource_id) a;

select count(1) num
  from (select t1.*
          from t_asset t1
          left join (select asset_code,
                           max(case
                                 when status in (1, 10, 11, 12) then
                                  1
                               end) as status,
                           max(case
                                 when (content_status = 1 or content_status = 4) then
                                  1
                               end) as content_status
                      from t_asset_file
                     group by asset_code) a1
            on (a1.asset_code = t1.resource_id)
         where 1 = 1
           and t1.type = 0
           and (t1.status in (1, 10, 11, 12, 100) or
               (t1.status in (3, 4, 8) and a1.status = 1))
           and (a1.content_status = 1 or a1.asset_code is null)
         order by t1.create_time desc, t1.resource_id) a;
/*用LEFT JOIN优化多个子查询（三）*/
select m.col1, d.col2, wmsys.wm_concat(m.col3) col3s
  from m, d
 where m.col3 = d.col3
   and m.col6 = 2
   and m.col7 < '201312'
   and m.col4 is null
   and (not exists (select 1
                      from m m1, d d1
                     where m1.col3 = d1.col3
                       and m1.col4 = m.col3
                       and d1.col2 = d.col2
                       and m1.col7 < '201312'
                       and m1.col6 = 3) or
        (select sum(d1.col5)
                      from m m1, d d1
                     where m1.col3 = d1.col3
                       and m1.col4 = m.col3
                       and d1.col2 = d.col2
                       and m1.col7 < '201312'
                       and m1.col6 = 3) < d.col5)
 group by m.col1, d.col2;

with x as
 (select m.col1, d.col2, m.col3, m.col4, d.col5, m.col6
    from m, d
   where m.col3 = d.col3
     and m.col6 in (2, 3)
     and m.col7 < '201312')
select a.col1, a.col2, wmsys.wm_concat(m.col3) col3s
  from x a
  left join (select b.bol4, b.col2 sum(b.col5) as col5
               from x b
              where b.col6 = 3
              group by b.col4, b.col2) b
    on (b.col4 = a.col3 and b.col2 = a.col2)
 where a.col4 is null
   and a.col6 = 2
   and (b.col4 is null or b.col5 < a.col5)
 group by a.col1, a.col2;
/*去掉EXISTS引起的FILTER*/
select .. .. .
  from o
 where exists (select 1
          from f
         where o.pty_id = f.pty_id
           and o.org = f.org
           and o.code = f.code)
   and o.xx || o.xx || o.xxx..
.not in (select f.xx || f.xx || f.xx .. .
                 from f
                where end_dt = to_date('29991231', 'YYYY-MM-DD')
                  and o.pty_id = pty_id
                  and o.org = org
                  and o.code = code);

select .. .. .
  from o,
       (select * from f where end_dt = to_date('29991231', 'YYYY-MM-DD')) f
 where o.pty_id = f.pty_id
   and o.code = f.code
   and o.xx || o.xx || o.xxx...... <> f.xx || f.xx || f.xx......;
/*重叠时间计数*/
create table procs(proc_id,anest_name,start_time,end_time) as
select 10, 'Baker', '08:00', '11:00'
  from dual
union all
select 20, 'Baker', '09:00', '13:00'
  from dual
union all
select 30, 'Dow', '09:00', '15:30'
  from dual
union all
select 40, 'Dow', '08:00', '13:30'
  from dual
union all
select 50, 'Dow', '10:00', '11:30'
  from dual
union all
select 60, 'Dow', '12:30', '13:30'
  from dual
union all
select 70, 'Dow', '13:30', '14:30'
  from dual
union all
select 80, 'Dow', '18:00', '19:00'
  from dual;

select anest_name, start_time as t
  from procs
union
select anest_name, end_time as t
  from procs;

select a.t, b.*, count(*) over(partition by a.anest_name, a.t) as ct
  from (select anest_name, start_time as t
          from procs
        union
        select anest_name, end_time as t
          from procs) a
 inner join procs b
    on (b.anest_name = a.anest_name and b.start_time <= a.t and
       b.end_time > a.t)
 order by 3, 1, 2;

select proc_id, anest_name, start_time, end_time, max(ct) as ct
  from (select a.t, b.*, count(*) over(partition by a.anest_name, a.t) as ct
          from (select anest_name, start_time as t
                  from procs
                union
                select anest_name, end_time as t
                  from procs) a
         inner join procs b
            on (b.anest_name = a.anest_name and b.start_time <= a.t and
               b.end_time > a.t))
 group by proc_id, anest_name, start_time, end_time
 order by 2, 1;

with x0 as
 (
  /*枚举时间点t*/
  select proc_id, anest_name, start_time, end_time, t
    from (select proc_id,
                  anest_name,
                  start_time,
                  end_time,
                  start_time as s,
                  end_time   as e
             from procs) unpivot(t for col in(s, e))),
x1 as
 (
  /*落实重叠行*/
  select (prior t) as t, proc_id, anest_name, start_time, end_time
    from x0
   where level = 2
  connect by nocycle(prior anest_name) = anest_name
         and start_time <= (prior t)
         and (prior t) < end_time
         and level <= 2
   order by 1),
x2 as
 ( /*计算重叠数目*/
  select proc_id,
          anest_name,
          start_time,
          end_time,
          t,
          count(distinct proc_id) over(partition by t, anest_name) as cnt
    from x1)
select proc_id, anest_name, start_time, end_time, max(cnt) as cnt
  from x2
 group by proc_id, anest_name, start_time, end_time
 order by 1;
/*用分析函数改写优化*/
/*病人对医疗机构提出法律索赔，记录表为Claims*/
create or replace view claims(索赔号,患者) as
select 10, 'Smith'
  from dual
union all
select 20, 'Jones'
  from dual
union all
select 30, 'Brown'
  from dual;
/*每一项索赔都有一个或多个被告(defendant)，通常都是医生，记录表为defendants*/
create or replace view defendants(索赔号,被告) as
select 10, 'Johnson'
  from dual
union all
select 10, 'Meyer'
  from dual
union all
select 10, 'Dow'
  from dual
union all
select 20, 'Baker'
  from dual
union all
select 20, 'Meyer'
  from dual
union all
select 30, 'Johnson'
  from dual;
/*每个与索赔相关的被告都有法律事件历史，当某项索赔的被告索赔状态发生变化时，都会记录下来*/
create or replace view legalevents(索赔号,被告,索赔状态,change_date) as
select 10, 'Johnson', 'AP', date '1994-01-01'
  from dual
union all
select 10, 'Johnson', 'OR', date '1994-02-01'
  from dual
union all
select 10, 'Johnson', 'SF', date '1994-03-01'
  from dual
union all
select 10, 'Johnson', 'CL', date '1994-04-01'
  from dual
union all
select 10, 'Meyer', 'AP', date '1994-01-01'
  from dual
union all
select 10, 'Meyer', 'OR', date '1994-02-01'
  from dual
union all
select 10, 'Meyer', 'SF', date '1994-03-01'
  from dual
union all
select 10, 'Dow', 'AP', date '1994-01-01'
  from dual
union all
select 10, 'Dow', 'OR', date '1994-02-01'
  from dual
union all
select 20, 'Meyer', 'AP', date '1994-01-01'
  from dual
union all
select 20, 'Meyer', 'OR', date '1994-02-01'
  from dual
union all
select 20, 'Baker', 'AP', date '1994-01-01'
  from dual
union all
select 30, 'Johnson', 'AP', date '1994-01-01'
  from dual;
/*对于每个被告索赔状态的变化按照法律制定的已知顺序进行，如下面的Claim状态表所示*/
create or replace view claimstatuscodes(索赔状态,索赔状态描述,顺序) as
select 'AP', 'Awaiting review panel', 1
  from dual
union all
select 'OR', 'Panel opinion rendered', 2
  from dual
union all
select 'SF', 'Suit filed', 3
  from dual
union all
select 'CL', 'Closed', 4
  from dual;
/*被告的索赔状态是他或她最近的索赔状态，是具有最高索赔顺序号的索赔状态*/
select e1.被告, c1.索赔号, c1.患者, s3.顺序, s3.索赔状态
  from claims c1
 inner join legalevents e1
    on e1.索赔号 = c1.索赔号
 inner join claimstatuscodes s3
    on s3.索赔状态 = e1.索赔状态
 order by 1, 2, 4;

select c1.索赔号, c1.患者, s1.索赔状态
  from claims c1, claimstatuscodes s1
 where s1.顺序 in (select min(s2.顺序)
                   from claimstatuscodes s2
                  where s2.顺序 in (select max(s3.顺序)
                                    from legalevents e1, claimstatuscodes s3
                                   where e1.索赔状态 = s3.索赔状态
                                     and e1.索赔号 = c1.索赔号
                                   group by e1.被告));

select 索赔号, 患者, 顺序, 索赔状态
  from (select 被告,
               索赔号,
               患者,
               顺序,
               索赔状态,
               /*2.查找所有被告索赔状态里最低的那个状态*/
               min(顺序) over(partition by 索赔号) as min_顺序
          from (select e1.被告,
                       c1.索赔号,
                       c1.患者,
                       s3.顺序,
                       s3.索赔状态,
                       /*1.查找所有被告的当前索赔状态*/
                       max(s3.顺序) over(partition by e1.被告, e1.索赔号) as max_顺序
                  from claims c1
                 inner join legalevents e1
                    on e1.索赔号 = c1.索赔号
                 inner join claimstatuscodes s3
                    on s3.索赔状态 = e1.索赔状态)
         where 顺序 = max_顺序)
 where 顺序 = min_顺序
 order by 1, 3;
--通过“左连”取出所有的数据
select c1.索赔号, c1.患者, s1.顺序, e1.索赔号
  from claims c1
 inner join defendants d1
    on c1.索赔号 = d1.索赔号
 cross join claimstatuscodes s1
  left join legalevents e1
    on (c1.索赔号 = e1.索赔号 and d1.被告 = e1.被告 and s1.索赔状态 = e1.索赔状态)
 order by 1, 3;
--通过最小未完成状态得到最后的完成状态
select c1.索赔号,
       c1.患者,
       case min(s1.顺序)
         when 2 then
          'AP'
         when 3 then
          'OR'
         when 4 then
          'SF'
         else
          'CL'
       end as 索赔状态
  from claims c1
 inner join defendants d1
    on c1.索赔号 = d1.索赔号
 cross join claimstatuscodes s1
  left join legalevents e1
    on (c1.索赔号 = e1.索赔号 and d1.被告 = e1.被告 and s1.索赔状态 = e1.索赔状态)
 where e1.索赔号 is null
 group by c1.索赔号, c1.患者;

select c1.索赔号,
       c1.患者,
       max(lag_status) keep(dense_rank first order by s1.顺序) as s
  from claims c1
 inner join defendants d1
    on c1.索赔号 = d1.索赔号
 cross join (select /*用lag取上一个状态*/
              lag(索赔状态) over(order by 顺序) as lag_status,
              索赔状态,
              顺序
               from claimstatuscodes) s1
  left outer join legalevents e1
    on (c1.索赔号 = e1.索赔号 and d1.被告 = e1.被告 and s1.索赔状态 = e1.索赔状态)
 where e1.索赔号 is null
 group by c1.索赔号, c1.患者;
/*相等集合之零件供应商*/
create table supparts(
供应商编码 char(2) not null,
零件编码 char(2) not null,
primary key (供应商编码,零件编码));

insert into supparts
  select '1', '1'
    from dual
  union all
  select '1', '2'
    from dual
  union all
  select '1', '3'
    from dual
  union all
  /**/
  select '2', '3'
    from dual
  union all
  select '2', '4'
    from dual
  union all
  select '2', '5'
    from dual
  union all
  /**/
  select '3', '1'
    from dual
  union all
  select '3', '2'
    from dual
  union all
  select '3', '3'
    from dual
  union all
  /**/
  select '4', '1'
    from dual
  union all
  select '4', '0'
    from dual
  union all
  select '4', '3'
    from dual
  union all
  /**/
  select '5', '1'
    from dual
  union all
  select '5', '2'
    from dual
  union all
  select '5', '5'
    from dual
  union all
  /**/
  select '6', '1'
    from dual
  union all
  select '6', '4'
    from dual
  union all
  select '6', '5'
    from dual;

select 供应商1, 供应商2
  from (select a.供应商编码 as 供应商1,
               b.供应商编码 as 供应商2,
               max(a.零件数) as 零件数,
               count(*) as 相同零件数
          from (select 供应商编码,
                       零件编码,
                       count(*) over(partition by 供应商编码) as 零件数
                  from supparts) a
         inner join supparts b
            on (a.零件编码 = b.零件编码 and a.供应商编码 < b.供应商编码)
         group by a.供应商编码, b.供应商编码)
 where 零件数 = 相同零件数;
/*相等集合之飞机棚与飞行员*/
create table pilotskills/*飞行员技能*/
(飞行员 char(15) not null,
飞机 char(15) not null,
primary key (飞行员,飞机));

insert into pilotskills
  select 'Celko', 'Piper Cub'
    from dual
  union all
  select 'Higgins', 'B-52 Bomber'
    from dual
  union all
  select 'Higgins', 'F-14 Fighter'
    from dual
  union all
  select 'Higgins', 'Piper Cub'
    from dual
  union all
  select 'Jones', 'B-52 Bomber'
    from dual
  union all
  select 'Jones', 'F-14 Fighter'
    from dual
  union all
  select 'Smith', 'B-1 Bomber'
    from dual
  union all
  select 'Smith', 'B-52 Bomber'
    from dual
  union all
  select 'Smith', 'F-14 Fighter'
    from dual
  union all
  select 'Wilson', 'B-1 Bomber'
    from dual
  union all
  select 'Wilson', 'F-52 Bomber'
    from dual
  union all
  select 'Wilson', 'F-14 Fighter'
    from dual
  union all
  select 'Wilson', 'F-17 Fighter'
    from dual;

create table hangar/*飞机棚*/
(飞机 char(15) primary key);

insert into hangar
  select 'B-1 Bomber'
    from dual
  union all
  select 'B-52 Bomber'
    from dual
  union all
  select 'F-14 Fighter'
    from dual;

select ps1.飞行员,
       count(ps1.飞机) as 飞行员会开的飞机数,
       count(h1.飞机) as 库中能开的飞机数,
       max(h1.机库里的飞机数) as 机库里的飞机数
  from pilotskills ps1
  left join (select count(*) over() as 机库里的飞机数, 飞机 from hangar) h1
    on (ps1.飞机 = h1.飞机)
 group by ps1.飞行员;

select *
  from (select ps1.飞行员,
               count(ps1.飞机) as 飞行员会开的飞机数,
               count(h1.飞机) as 库中能开的飞机数,
               max(h1.机库里的飞机数) as 机库里的飞机数
          from pilotskills ps1
          left join (select count(*) over() as 机库里的飞机数, 飞机
                      from hangar) h1
            on (ps1.飞机 = h1.飞机)
         group by ps1.飞行员)
 where 库中能开的飞机数 = 机库里的飞机数;

select *
  from (select ps1.飞行员,
               count(ps1.飞机) as 飞行员会开的飞机数,
               count(h1.飞机) as 库中能开的飞机数,
               max(h1.机库里的飞机数) as 机库里的飞机数
          from pilotskills ps1
          left join (select count(*) over() as 机库里的飞机数, 飞机
                      from hangar) h1
            on (ps1.飞机 = h1.飞机)
         group by ps1.飞行员)
 where 库中能开的飞机数 = 机库里的飞机数 /*会开*/
   and 飞行员会开的飞机数 = 机库里的飞机数 /*只会开*/;
/*用分析函数改写最值过滤条件*/
select emp_name, sum(h1.bill_hrs * b1.bill_rate) as totalcharges
  from consultants c1, billings b1, hoursworked h1
 where c1.emp_id = b1.emp_id
   and c1.emp_id = h1.emp_id
   and bill_date = (select max(bill_date)
                      from billings b2
                     where b2.emp_id = c1.emp_id
                       and b2.bill_date <= h1.work_date)
   and h1.work_date >= b1.bill_date
 group by emp_name;

select b.emp_name, a.totalcharges
  from (select emp_id, sum(bill_rate * bill_hrs) as totalcharges
          from (select b1.bill_rate,
                       b1.emp_id,
                       h1.bill_hrs,
                       rank() over(partition by b1.emp_id, h1.work_date order by b1.bill_date desc) as sn
                  from billings b1, hoursworked h1
                 where b1.emp_id = h1.emp_id
                   and h1.work_date >= b1.bill_date)
         where sn = 1
         group by emp_id) a
 inner join consultants b
    on b.emp_id = a.emp_id;
/*用树查询找指定级别的数据*/
create table emp_level as
select empno, level as lv
  from emp
 start with mgr is null
connect by (prior empno) = mgr;

select a.empno, a.ename, a.mgr, b.lv
  from emp a
  left join emp_level b
    on (b.empno = a.empno)
 where b.lv = 2
 start with a.empno = 7876
connect by ((prior a.mgr) = a.empno and (prior b.lv) > 2);
/*行转列与列转行*/
create table t(a varchar2(30),b varchar2(30),c varchar2(30),d varchar2(30));

insert into t values ('门店1', '品牌1', '2', '8');
insert into t values ('门店1', '品牌2', '3', '6');
insert into t values ('门店1', '品牌3', '2', '10');
insert into t values ('门店2', '品牌1', '1', '4');
insert into t values ('门店2', '品牌2', '4', '8');
insert into t values ('门店2', '品牌3', '4', '20');
insert into t values ('门店3', '品牌1', '3', '12');
insert into t values ('门店3', '品牌2', '2', '4');
insert into t values ('门店3', '品牌3', '1', '5');

with t1 as
/*1.行转列*/
 (select grouping(t.b) as gp_b,
         t.b as 品牌,
         sum(case t.a
               when '门店1' then
                t.c
             end) as 销量_门店1,
         sum(case t.a
               when '门店2' then
                t.c
             end) as 销量_门店2,
         sum(case t.a
               when '门店3' then
                t.c
             end) as 销量_门店3,
         sum(t.c) as 销量_合计,
         sum(case t.a
               when '门店1' then
                t.d
             end) as 收入_门店1,
         sum(case t.a
               when '门店2' then
                t.d
             end) as 收入_门店2,
         sum(case t.a
               when '门店3' then
                t.d
             end) as 收入_门店3,
         sum(t.d) as 收入_合计
    from t
   group by rollup(t.b)
   order by 1 desc, 2)
/*2.列转行*/
select case
         when gp_b = 1 then
          '销量合计'
         else
          品牌
       end as 品牌,
       销量_门店1 as 门店1,
       销量_门店2 as 门店2,
       销量_门店3 as 门店3,
       销量_合计 as 合计
  from t1
union all
select case
         when gp_b = 1 then
          '收入合计'
         else
          品牌
       end as 品牌,
       收入_门店1 as 门店1,
       收入_门店2 as 门店2,
       收入_门店3,
       收入_合计 as 合计
  from t1
union all
select case
         when gp_b = 1 then
          '收入合计'
         else
          品牌
       end as 品牌,
       round(销量_门店1 * 收入_门店1 / 销量_合计, 2) as 门店1,
       round(销量_门店2 * 收入_门店1 / 销量_合计, 2) as 门店2,
       round(销量_门店3 * 收入_门店1 / 销量_合计, 2) as 门店3,
       收入_合计 as 合计
  from t1;
/*UPDATE、ROW_NUMBER与MERGE*/
create table emp2 as select * from emp where deptno = 10;
alter table emp2 add constraints pk_emp2 primary key(empno);

update emp2 a
   set a.empno =
       (select row_number() over(order by b.hiredate) empno
          from emp2 b
         where b.empno = a.empno);

alter table emp2 drop constraints pk_emp2;

update emp2 a
   set a.empno =
       (select row_number() over(order by b.hiredate) empno
          from emp2 b
         where b.empno = a.empno);
select empno, ename from emp2;

update emp2 a
   set a.empno =
       (select new_no
          from (select row_number() over(order by b.hiredate) new_no
                  from emp2 b) b
         where b.rowid = a.rowid);
select empno, ename, hiredate from emp2 order by 3;

merge into emp2 a
using (select b.rowid as rid,
              row_number() over(order by b.hiredate) as empno
         from emp2 b) b
on (b.rowid = a.rowid)
when matched then
  update set a.empno = b.empno;
/*改写优化UPDATE语句*/
update t_0 a
   set a.status = -1
 where exists (select 1
          from (select m.u, count(*) cnt
                  from t_m m, t_0 n
                 where m.st > trunc(sysdate - 1)
                   and m.p = substr(n.u, -2, 2)
                   and m.u = n.u
                 group by m.u
                having conut(*) > -1) b
         where a.u = b.u);

update t_0 a
   set a.status = -1
 where exists (select 1
          from (select m.u, count(*) cnt
                  from t_m m, t_0 n
                 where m.st > trunc(sysdate - 1)
                   and m.p = substr(m.u, -2, 2)
                   and m.u = n.u
                 group by m.u) b
         where a.u = b.u);

update t_0 a
   set a.status = -1
 where a.u in (select m.u
                 from t_m m
                where m.st >= trunc(sysdate - 1)
                  and m.p = substr(m.u, -2, 2));
/*改写优化UNION ALL语句*/
select count(*)
  from (select col1,
               col27,
               col28,
               col2,
               col3,
               col4,
               col5,
               col6,
               col7,
               col8,
               col9,
               col10,
               col20,
               col21,
               col22,
               col23,
               col24.col25,
               col26
          from ((select l.col1,
                        o.col2_1 as col27,
                        o.col28  as col28,
                        l.col2,
                        l.col3,
                        l.col4,
                        l.col5,
                        l.col6,
                        l.col7,
                        l.col8,
                        l.col9,
                        l.col10,
                        l.col20,
                        l.col21,
                        l.col22,
                        l.col23,
                        l.col24,
                        l.col25,
                        l.col26
                   from l, o
                  where l.col4 = 'o'
                    and l.col5 = o.o_id) union all
                (select l.col1,
                        a.col2_1 as col27,
                        a.col28,
                        as       col28,
                        l.col2,
                        l.col3,
                        l.col4,
                        l.col5,
                        l.col6,
                        l.col7,
                        l.col8,
                        l.col9,
                        l.col10,
                        l.col20,
                        l.col21,
                        l.col22,
                        l.col23,
                        l.col24,
                        l.col25,
                        l.col26
                   from l, a
                  where l.col4 = 'A'
                    and l.col5 = a.a_id) union all select l.col1, p.col2_1 as
                col27, '' as col28, l.col2, l.col3, l.col4, l.col5, l.col6,
                l.col7, l.col8, l.col9, l.col10, l.col20, l.col21, l.col22,
                l.col23, l.col24, l.col25, l.col26 from l, p where
                l.col4 = 'P' and l.col5 = p.p_id)) t
 where (col2 = 'TCBANK' or col3 = 'SELLERWANG')) xt100;

select count(*)
  from (select l.col1,
               t.col27,
               t.col28,
               l.col2,
               l.col3,
               l.col4,
               l.col5,
               l.col6,
               l.col7,
               l.col8,
               l.col9,
               l.col10,
               l.col20,
               l.col21,
               l.col22,
               l.col23,
               l.col24,
               l.col25,
               l.col26
          from l
         inner join (select o_id,
                           col2_1 as col27,
                           col28 as col28,
                           'O' as col4
                      from o
                    union all
                    select a_id as o_id,
                           a.col2_1 as col27,
                           a.col28 as col28,
                           'A' as col4
                      from a
                    union all
                    select p_id as o_id,
                           a.col3_1 as col27,
                           '' as col28,
                           'P' as col4
                      from p) t
            on (t.o_id = l.col5 and t.col4 = a.col4)
         where l.col4 in ('O', 'A', 'P')
           and (l.col2 = 'TCBANK' or l.col3 = 'SELLERWANG')) xt100;
/*纠结的MERGE语句*/
merge into a
using (select t.id, t.cp_id, t.cp_name, t.price
         from t, a t1
        where t1.b_date = '20130101'
          and t.b_date = '20130101'
          and t1.state = 0
        group by t.id, t.cp_id, t.cp_name, t.price) nq
on (ng.id = a.id)
when matched then
  update
     set a.cp_id = ng.cp_id, a.cp_name = ng.cp_name, a.value3 = ng.price
   where a.b_date = '20130101';

merge into a
using (select /*+ opt_param('_optimizer_mjc_enabled','false')*/
        t.id, t.cp_id, t.cp_name, t.price
         from t, a t1
        where t.id = t1.id t1.b_date = '20130101'
          and t.b_date = '20130101'
          and t1.state = 0
        group by t.id, t.cp_id, t.cp_name, t.price) nq
on (ng.id = a.id)
when matched then
  update
     set a.cp_id = ng.cp_id, a.cp_name = ng.cp_name, a.value3 = ng.price
   where a.b_date = '20130101';

update a
   set (a.cp_id, a.cp_name, a.value3) =
       (select t.cp_id, t.cp_name, t.price
          from t
         where t.id = a.id
           and t.b_date = '20130101')
 where a.b_date = '20130101'
   and a.state = 0
   and exists (select t.cp_id, t.cp_name, t.price
          from t
         where t.id = a.id
           and t.b_date = '20130101');

merge into (select *
              from a
             where b_date = '20130101'
               and state = 0) a
using (select /*+ opt_param('_optimizer_mjc_enabled','false')*/
        t.id, t.cp_id, t.cp_name, t.price
         from t
        where t.b_date = '20130101'
        group by t.id, t.cp_id, t.cp_name, t.price) ng
on (ng.id = a.id)
when matched then
  update
     set a.cp_id = ng.cp_id, a.cp_name = ng.cp_name, a.value3 = ng.price;
/*用CASE WHEN去掉UNION ALL*/
--太长了不想写。。。大体语句
select xxx
  from (select xxx from bi s union all select xxx from bi s) a
union all
select xxx
  from (select xxx from bi s union all select xxx from bi s) a
union all
select xxx
  from (select xxx from bi s union all select xxx from bi s) a;
/*不恰当的WITH及标量子查询*/
with wzxfl as
 (select *
    from (select c.用户号,
                 df.总数量,
                 row_number() over(partition by c.类型 order by df.总数量 desc) as 排名
            from customer c,
                 (select d.用户号, nvl(sum(d.总数量), '0') 总数量
                    from t_money d
                   where d.mon >= 201301
                     and d.mon <= 201309
                   group by d.用户号) df
           where c.用户号 = df.用户号
             and c.用户类型 in ('10', '11', '20')) cc
   where cc.排名 <= 3000)
select c.用户号,
       max(c.用户),
       max(c.地址),
       max(c.col11),
       max(h.总数量) 总数量13,
       max(h.排名) 排名,
       (select sum(d.总金额)
          from t_money d
         where d.用户号 = c.用户号
           and d.mon >= 201301
           and d.mon <= 201307
         group by d.用户号) as 总金额13,
       (select sum(d.总数量)
          from t_money d
         where d.用户号 = c.用户号
           and d.mon >= 201201
           and d.mon <= 201207
         group by d.用户号) as 总数量12,
       (select sum(d.总金额)
          from t_money d
         where d.用户号 = c.用户号
           and d.mon >= 201201
           and d.mon <= 201207
         group by d.用户号) as 总金额12
  from customer c, wzxfl h
 where c.用户号 = h.用户号
 group by c.用户号;

select d.用户号,
       nvl(sum(d.总数量), '0') 总数量,
       sum(case
             when mon <= 201307 then
              d.总金额
           end) as 总金额
  from t_money d
 where d.mon >= 201301
   and d.mon <= 201309
 group by d.用户号;

select c.*,
       df.总数量,
       df.总金额,
       row_number() over(partition by c.类型 order by df.总数量 desc) as 排名
  from customer c, (.. .) df
 where c.用户号 = df.用户号
   and c.用户类型 in ('10', '11', '20');

select d.用户号, sum(d.总数量) as 总数量, sum(d.总金额) as 总金额
  from t_money d
 where d.mon >= 201201
   and d.mon <= 201207
 group by d.用户号;

select df13.用户号,
       df13.用户,
       df13.地址,
       df13.col11,
       df13.总数量,
       df13.排名,
       nvl(df13.总金额, 0),
       nvl(df12.总数量, 0),
       nvl(df12.总金额, 0)
  from (select *
          from (select c.*,
                       df.总数量,
                       df.总金额,
                       row_number() over(partition by c.类型 order by df.总数量 desc) as 排名
                  from customer c (select d.用户号,
                                          nvl(sum(d.总数量), 0) 总数量,
                                          sum(case
                                                when mon <= 201307 then
                                                 d.总金额
                                              end) as 总金额
                                     from t_money d
                                    where d.mon >= 201301
                                      and d.mon <= 201309
                                    group by d.用户号) df
                 where c.用户号 = df.用户号
                   and c.用户类型 in ('10', '11', '12')) cc
         where cc.排名 <= 3000) df13
  left join (select d.用户号,
                    sum(d.总数量) as 总数量,
                    sum(d.总金额) as 总金额
               from t_money d
              where d.mon >= 201201
                and d.mon <= 201207
              group by d.用户号) df12
    on df12.用户号 = df13.用户号;
/*用分析函数加“行转列”来优化标量子查询*/
select *
  from (select userid,
               username,
               count(userid) as usercount,
               (case
                 when (count(userid)) -
                      (select count(1)
                         from dbo.t1 as c
                        where userid = a.userid
                          and state in (0, 2)
                          and createtime between '2013.12.07 00:00:00.000' and
                              '2013.12.19 23:59:59.999') <= 0 then
                  0
                 else
                  ((select count(*)
                      from dbo.t1 as b
                     where not exists
                     (select id from dbo.t2 where oldtaskid = b.taskid)
                       and (b.state = 1 or b.state = 6)
                       and b.userid = a.userid
                       and createtime between '2013.12.07 00:00:00.000' and
                           '2013.12.19 23:59:59.999') / count(userid)) -
                  (select count(1)
                     from dbo.t1 as c
                    where userid = a.userid
                      and state in (0, 2)) * 100
               end) as correctrate,
               row_number() over(order by a.userid desc) rownum
          from dbo.t1 as a
         where not exists (select id from dbo.t2 where oldtaskid = a.taskid)
           and createtime between '2013.12.07 00:00:00.000' and
               '2013.12.19 23:59:59.999'
         group by userid, username) as z
 where rownum between 41 and 60;

with x1 as
 (select count(case
                 when state in (0, 2) then
                  userid
               end) over(partition by userid) as ct2,
         userid,
         username,
         taskid,
         state,
         createtime
    from t1),
x2 as
 (select count(case
                 when state in (0, 2) then
                  userid
               end) over(partition by userid) as ct1,
         x1.*
    from x1
   where createtime between '2013.12.07 00:00:00.000' and
         '2013.12.19 23:59:59.999')
select *
  from (select userid,
               username,
               count(userid) as usercount,
               (case
                 when (count(userid)) - max(a.ct1) <= 0 then
                  0
                 else
                  (count(case
                           when state in (1, 6) then
                            userid
                         end) / (count(userid)) - max(a.ct2)) * 100
               end) as correctrate,
               row_number() over(order by a.userid desc) as rn
          from x2 a
         where not exists (select id from t2 where oldtaskid = a.taskid)
         group by userid, username) z
 where rn between 41 and 60;
/*用分析函数处理问题*/
create table t(comdate date,transdate date,amount number);

insert into t
  (comdate, transdate, amount)
values
  (date '2013-01-31', date '2013-01-01', 1);
insert into t
  (comdate, transdate, amount)
values
  (date '2013-01-31', date '2013-01-02', 2);
insert into t
  (comdate, transdate, amount)
values
  (date '2013-02-28', date '2013-02-01', 11);
insert into t
  (comdate, transdate, amount)
values
  (date '2013-02-28', date '2013-02-02', 12);
/*
comdate--每月最后一天
transdate--每天的日期
amount--交易金额
要求查出:
每天的日期、
交易金额、
本月每日平均交易金额、
上月每日平均交易金额
表里存的是一天一条记录
*/
select comdate,
       transdate,
       amount,
       (select avg(b.amount) from t b where b.comdate = a.comdate) as 本月日均,
       (select avg(b.amount)
          from t b
         where b.comdate = add_months(a.comdate, -1)) as 上月日均
  from t a
 order by 2;

select transdate as 日期,
       amount as 交易金额,
       avg(amount) over(partition by comdate) as 本月日均
  from t;

select transdate as 日期,
       amount as 交易金额,
       avg(amount) over(partition by comdate) as 本月日均,
       /*按月分组，按日期排序，取出当前行在本月的序号*/
       row_number() over(partition by comdate order by transdate) as seq
  from t;

select 日期,
       交易金额,
       本月日均,
       /*按内层取出的序号前移就是上一个月的数据*/
       lag(本月日均, seq) over(order by 日期) as 上月日均
  from (select transdate as 日期,
               amount as 交易金额,
               avg(amount) over(partition by comdate) as 本月日均,
               /*按月分组，按日期排序，取出当前行在本月的序号*/
               row_number() over(partition by comdate order by transdate) as seq
          from t);
/*用列转行改写A表多列关联B表同列*/
select gcc.segment1,
       ffv1.description,
       gcc.segment3,
       ffv3.description,
       gcc.segment2,
       ffv2.description,
       gcc.segment4,
       ffv4.description,
       gcc.segment5,
       ffv5.description,
       gcc.segment6,
       ffv6.description,
       gcc.segment7,
       gbb.period_name,
       gbb.period_net_dr - gbb.period_net_cr b_amount
  from apps.gl_balances         gbb,
       app.gl_code_combinatoins gcc,
       app.fnd_flex_values_vl   ffv1,
       app.fnd_flex_values_vl   ffv2,
       app.fnd_flex_values_vl   ffv3,
       app.fnd_flex_values_vl   ffv4,
       app.fnd_flex_values_vl   ffv5,
       app.fnd_flex_values_vl   ffv6,
       app.fnd_flex_values_sets ffvs1,
       app.fnd_flex_values_sets ffvs2,
       app.fnd_flex_values_sets ffvs3,
       app.fnd_flex_values_sets ffvs4,
       app.fnd_flex_values_sets ffvs5,
       app.fnd_flex_values_sets ffvs6
 where gbb.period_name = '2014-01'
   and gbb.actual_flag = 'B'
   and gbb.template_id is null
   and gbb.currency_code = 'CNY'
   and gbb.code_combination_id = gcc.code_combination_id
   and ffv1.flex_value = gcc.segment1
   and ffv1.flex_value = gcc.segment2
   and ffv1.flex_value = gcc.segment3
   and ffv1.flex_value = gcc.segment4
   and ffv1.flex_value = gcc.segment5
   and ffv1.flex_value = gcc.segment6
   and ffv1.flex_value_set_id = ffvs1.flex_value_set_id
   and ffvs1.flex_value_set_name = 'JI_COA_COM'
   and ffv1.flex_value_set_id = ffvs2.flex_value_set_id
   and ffvs2.flex_value_set_name = 'JI_COA_CST'
   and ffv1.flex_value_set_id = ffvs3.flex_value_set_id
   and ffvs3.flex_value_set_name = 'JI_COA_ACC'
   and ffv4.flex_value_set_id = ffvs4.flex_value_set_id
   and ffvs4.flex_value_set_name = 'JI_COA_BRD'
   and ffv5.flex_value_set_id = ffvs5.flex_value_set_id
   and ffvs5.flex_value_set_name = 'JI_COA_PRJ'
   and ffv6.flex_value_set_id = ffvs6.flex_value_set_id
   and ffvs6.flex_value_set_name = 'JI_COA_ICP'
   and gbb.period_net_dr - gbb.period_net_cr <> 0
 order by 1, 3;

with gcc0 as
 (select rownum as sn,
         gcc.segment1,
         gcc.segment2,
         gcc.segment3,
         gcc.segment4,
         gcc.segment5,
         gcc.segment6,
         gcc.segment7,
         gbb.period_name,
         gbb.period_net_dr - gbb.period_net_cr b_amount
    from apps.gl_balances gbb, apps.gl_code_combinations gcc
   where gbb.period_name = '2014-01'
     and gbb.actual_flag = 'B'
     and gbb.template_id is null
     and gbb.currency_code = 'CNY'
     and gbb.code_combination_id = gcc.code_combination_id
     and gbb.period_net_dr <> gbb.period_net_cr),
gcc as
 (select sn, flex_value_set_name, segment0, segment7, period_name, b_amount
    from gcc0 unpivot(segment0 for flex_value_set_name in(segment1 as
                                                          'JI_COA_COM',
                                                          segment2 as
                                                          'JI_COA_CST',
                                                          segment3 as
                                                          'JI_COA_ACC',
                                                          segment4 as
                                                          'JI_COA_BRD',
                                                          segment5 as
                                                          'JI_COA_PRJ',
                                                          segment6 as
                                                          'JI_COA_ICP')))
select max(case gcc.flex_value_set_name
             when 'JI_COA_COM' then
              gcc.segment0
           end) as segment1,
       max(case gcc.flex_value_set_name
             when 'JI_COA_CST' then
              gcc.segment0
           end) as segment1,
       max(case gcc.flex_value_set_name
             when 'JI_COA_ACC' then
              gcc.segment0
           end) as segment1,
       max(case gcc.flex_value_set_name
             when 'JI_COA_BRD' then
              gcc.segment0
           end) as segment1,
       max(case gcc.flex_value_set_name
             when 'JI_COA_PRJ' then
              gcc.segment0
           end) as segment1,
       max(case gcc.flex_value_set_name
             when 'JI_COA_ICP' then
              gcc.segment0
           end) as segment1,
       max(case gcc.flex_value_set_name
             when 'JI_COA_COM' then
              ffv.description
           end) as des1,
       max(case gcc.flex_value_set_name
             when 'JI_COA_CST' then
              ffv.description
           end) as des2,
       max(case gcc.flex_value_set_name
             when 'JI_COA_ACC' then
              ffv.description
           end) as des3,
       max(case gcc.flex_value_set_name
             when 'JI_COA_BRD' then
              ffv.description
           end) as des4,
       max(case gcc.flex_value_set_name
             when 'JI_COA_PRJ' then
              ffv.description
           end) as des5,
       max(case gcc.flex_value_set_name
             when 'JI_COA_ICP' then
              ffv.description
           end) as des6,
       max(gcc.segment7) as segment7,
       max(gcc.period_name) as period_name,
       max(gcc.b_amount) as b_amount
  from gcc, apps.fnd_flex_values_vl ffv, apps.fnd_flex_value_sets ffvs
 where ffv.flex_value = gcc.segment0
   and ffv.flex_value_set_id = ffvs.flex_value_set_id
   and ffv.flex_value_set_name = gcc.flex_value_set_name
 group by gcc.sn
having count (*) = 6;
/*用分析函数改写最值语句*/
select a.deptno, a.min_no, mi.ename min_n, ma.empno as max_n
  from (select deptno, min(empno) as min_no, max(empno) as max_no
          from emp
         group by deptno) a
 inner join emp mi
    on (mi.deptno = a.deptno and mi.empno = a.min_no)
 inner join emp ma
    on (ma.deptno = a.deptno and ma.empno = a.max_no);

select deptno,
       min(empno) as min_no,
       max(ename) keep(dense_rank first order by empno) as min_n,
       max(empno) as max_no,
       max(ename) keep(dense_rank last order by empno) as min_n
  from emp
 group by deptno;

select a.deptno, a.min_sal, mi.ename, a.max_sal, ma.ename
  from (select deptno, min(sal) as min_sal, max(sal) as max_sal
          from emp
         group by deptno) a
 inner join emp mi
    on (mi.deptno = a.deptno and mi.empno = a.min_sal)
 inner join emp ma
    on (ma.deptno = a.deptno and ma.sal = a.max_sal)
 order by 1;

select deptno,
       min(sal) as min_sal,
       max(ename) keep(dense_rank first order by sal) as min_n,
       max(sal) as max_sal,
       max(ename) keep(dense_rank last order by sal) as min_n
  from emp
 group by deptno;

with x0 as
 (select deptno,
         sal,
         ename,
         min(sal) over(partition by deptno) as min_sal,
         max(sal) over(partition by deptno) as max_sal
    from emp)
select a.deptno, a.min_sal, a.ename as min_n, b.max_sal, b.ename as max_n
  from x0 a, x0 b
 where a.deptno = b.deptno
   and a.sal = a.min_sal
   and b.sal = b.max_sal
 group by a.deptno, a.min_sal, a.ename, b.max_sal, b.ename;
/*多列关联的半连接与索引*/
create table emp2 as
select ename, job, sal, comm from emp where job = 'CLERK';
create index idx_emp_name on emp(ename);

select empno, ename, job, sal, deptno
  from emp
 where (ename || job || sal) in (select ename || job || sal from emp2);

select empno, ename, job, sal, deptno
  from emp
 where (ename, job, sal) in (select ename, job, sal from emp2);
/*巧用分析函数优化自关联*/
create table t as
select '2' as col1, '4' as col2
  from dual
union all
select '1' as col1, '5' as col2
  from dual
union all
select '1' as col1, '5' as col2
  from dual
union all
select '2' as col1, '5' as col2
  from dual
union all
select '3' as col1, '3' as col2
  from dual
union all
select '12' as col1, '16' as col2
  from dual
union all
select '11' as col1, '15' as col2
  from dual
union all
select '13' as col1, '13' as col2
  from dual
union all
select '12' as col1, '17' as col2
  from dual;

select to_char(lengthb(col2), 'FM000') || chr(0) num_length, col1, col2
  from t
 where not exists (select 1
          from t a
         where a.col1 <= t.col1
           and a.col2 >= t.col2
           and (t.col1 != a.col1 or t.col2 != a.col2)
           and lengthb(a.col1) = lengthb(t.col1));

select to_char(lengthb(t.col2), 'FM000') || chr(0) num_length,
       t.col1,
       t.col2
  from t
  left join t a
    on (a.col1 <= t.col1 and a.col2 >= t.col2 and
       (t.col1 != a.col1 or t.col2 != a.col2) and
       lengthb(a.col1) = lengthb(t.col1))
 where a.col1 is null;

select lengthb(col1) lb,
       col1,
       col2,
       /*按长度分组，按col1排序，累计取col2的最大值*/
       max(col2) over(partition by lengthb(col1) order by col1) as max_col2
  from t
 order by 1, 2;

select a.*
  from (select lengthb(col1) lb,
               col1,
               col2,
               /*按长度分组，按col1排序，累计取col2的最大值*/
               max(col2) over(partition by lengthb(col1) order by col1) as max_col2
          from t) a
/*增加条件，取范围截止值*/
 where col2 >= max_col2
 order by 1, 2;

select *
  from (
        /*按截止时间分组，对起始时间排序，生成序号，因可能有重复数据，这里用了rank*/
        select rank() over(partition by col2 order by col1) as seq, a.*
          from (select lengthb(col1) lb,
                        col1,
                        col2,
                        /*按长度分组，按col1排序，累计取col2的最大值*/
                        max(col2) over(partition by lengthb(col1) order by col1) as max_col2
                   from t) a
        /*增加条件，取范围截止值*/
         where col2 >= max_col2)
 where seq = 1;
/*纠结的UPDATE语句*/
update k
   set k.flag = 1
 where id in (select c.id
                from k c
               where c.month = '201312'
                 and c.qty = 0
                 and not exists (select m.ename
                        from (select n.ename, count(1) cs
                                from k n
                               where n.month = '201312'
                                 and n.eclass in ('A', 'B')
                               group by n.ename) m
                       where m.cs > 1
                         and m.ename = c.ename)
              union all
              select b.id
                from k b
               where b.month = '201312'
                 and b.qty is null
                 and not exists (select a.ename
                        from (select t.ename, count(1) cs
                                from k t
                               where t.month = '201312'
                                 and t.eclass in ('A', 'B')
                               group by t.ename) a
                       where a.cs > 1
                         and a.ename = b.ename));

select c.id
  from k c
 where c.month = '201312'
   and nvl(c.qty, 0) = 0
   and not exists (select m.ename
          from (select n.ename, count(1) cs
                  from k n
                 where n.month = '201312'
                   and n.eclass in ('A', 'B')
                 group by n.ename) m
         where m.cs > 1
           and m.ename = c.ename);

update k c
   set c.flag = 1
 where rowid in (select rid
                   from (select rowid as rid,
                                qty,
                                count(case
                                        when eclass in ('A', 'B') then
                                         eclass
                                      end) over(partition by ename) as cs
                           from k x
                          where x.month = '201312')
                  where nvl(qty, 0) = 0
                    and cs <= 1);
/*巧用JOIN条件合并UNION ALL语句*/
select msi.*
  from inv.msi@link msi
 where 1 = 1
   and msi.flag = 'Y'
   and msi.o_id in (170, 572, 953, 242, 240, 1052, 1131)
   and msi.last_update_date between (date '2012-1-1') and (date '2013-1-1')
   and not exists (select null
          from inv.msi b, apps.hao
         where b.o_id = hao.o_id
           and msi.segment1 = b.segment1
           and hao.attribute1 = msi.o_id)
union all
select msi.*
  from inv.msi@link msi
 where 1 = 1
   and msi.flag = 'N'
   and msi.o_id in (170, 572, 953, 242, 240, 1052, 1131)
   and msi.last_update_date between (date '2012-1-1') and (date '2013-1-1')
   and exists (select null
          from apps.mi@link mi
         where 1 = 1
           and mi.inventory_item_id = msi.inventory_item_id
           and mi.o_id = msi.o_id)
   and not exists (select null
          from inv.msi b, apps.hao
         where b.o_id = hao.o_id
           and msi.segment1 = b.segment1
           and hao.attribute1 = msi.o_id);

select msi.*
  from inv.msi@link msi
  left join apps.mi@link mi
    on (mi.inventory_item_id = msi.inventory_item_id and mi.o_id = msi.o_id)
 where 1 = 1
   and msi.flag = 'N'
   and mi.inventory_item_id is not null
   and msi.o_id in (170, 572, 953, 242, 240, 1052, 1131)
   and msi.last_update_date between (date '2012-1-1') and (date '2013-1-1')
   and exists (select null
          from apps.mi@link mi
         where 1 = 1
           and mi.inventory_item_id = msi.inventory_item_id
           and mi.o_id = msi.o_id)
   and not exists (select null
          from inv.msi b, apps.hao
         where b.o_id = hao.o_id
           and msi.segment1 = b.segment1
           and hao.attribute1 = msi.o_id);

select a.*
  from (select msi.*
          from inv.msi@link msi
          left join apps.mi@link mi
            on (msi.flag = 'N' and
               mi.inventory_item_id = msi.inventory_item_id and
               mi.o_id = msi.o_id)
         where msi.o_id in (170, 572, 953, 242, 240, 1052, 1131)
           and msi.last_update_date between (date '2012-1-1') and
               (date '2013-1-1')
           and msi.flag = 'Y'
            or mi.inventory_item_id is not null) a,
       (select b, segment1, hao.attribute1
          from apps.hao, inv.msi b
         where b.o_id = hao.o_id) b
 where a.segment1 = b.segment1(+)
   and b.attribute1 is null;
/*用分析函数去掉NOT IN*/
select customer.c_cust_id, card.type card.n_all_money
  from yd_vip.card
 where card.c_cust_id not in (select c_cust_id
                                from yd_vip.card
                               where type in ('11', '12', '13', '14')
                                 and flag = '1')
   and card.type in ('11', '12', '13', '14')
   and card.flag = 'F';

select a.cust_income_level, a.cust_id, a.cust_first_name, a.cust_last_name
  from sh.customers a
 where a.cust_city = 'Aachen'
      /*where type in ('11', '12', '13', '14')*/
   and a.cust_income_level not in
       (select b.cust_income_level
          from sh.customers b
         where
        /*where type in ('11', '12', '13', '14')*/
         b.cust_city = 'Celle');

with c as
 (select a.cust_income_level,
         a.cust_id,
         a.cust_first_name,
         a.cust_last_name,
         a.cust_city
    from sh.customers a
   where a.cust_city in ('Aachen', 'Celle')
  /*and type in ('11', '12', '13', '14')*/
  )
select *
  from c a
 where a.cust_city = 'Aachen'
   and a.cust_income_level not in
       (select b.cust_income_level from c b where b.cust_city = 'Celle');

select a.cust_income_level,
       a.cust_id,
       a.cust_first_name,
       a.cust_last_name,
       min(cust_city) over(partition by cust_income_level) as min_city,
       max(cust_city) over(partition by cust_income_level) as max_city
  from sh.customers a
 where a.cust_city in ('Aachen', 'Celle')
   and rownum <= 3;

select a.cust_income_level, a.cust_id, a.cust_first_name, a.cust_last_name
  from (select a.cust_income_level,
               a.cust_id,
               a.cust_first_name,
               a.cust_last_name,
               max(cust_city) over(partition by cust_income_level) as max_city
          from sh.customers a
         where a.cust_city in ('Aachen', 'Celle')) a
 where max_city = 'Aachen';
/*读懂查询中的需求之裁剪语句*/
select id
  from (select a.id, count(b.id) cnt
          from a, b
         where a.id = b.cid(+)
           and a.status = 0
         group by a.id
        having count(*) <= (select min(count(*))
                             from b
                            where cid in
                                  (select id from a where status != 1)
                            group by cid)
         order by cnt, id)
 where rownum < 2;

select id
  from (select a.id, count(b.id) cnt
          from a, b
         where a.id = b.cid(+)
           and a.status = 0
         group by a.id
        having count(*) <= (select min(count(b.id))
                             from b
                            inner join a
                               on a.id = b.cid
                            where a.status = 0
                            group by cid)
         order by cnt, id)
 where rownum < 2;

select id
  from (select a.id, count(b.id) cnt
          from a, b
         where a.id = b.cid(+)
           and a.status = 0
         group by a.id
         order by cnt, id)
 where rownum < 2;
/*去掉FILTER里的EXISTS之活学活用*/
select /*+first_rows*/
 t0.ani as col_0_0_, count(t0.id) as col_1_0_
  from t0
 where (t0.task_id is not null)
   and ((t0.type > 1 or t0.type < 1) and
       t0.dbdt < current_timetamp - (8 / 24) and
       (t0.ani in ('列表1') or concat('0', t0.ani) in ('列表1')) or
       t0.type = 1 and
       (exists
        (select /*+no_unnest*/
           t1.id
            from t1
           where t1.id = t0.lead_interaction_id
             and t1.begin_date >=
                 to_date('2014-08-08 00:00:00', 'yyyy-MM-dd HH24:mi:ss')
             and t1.begin_date <=
                 to_date('2014-08-08 23:59:59', 'yyyy-MM-dd HH24:mi:ss'))) and
       (t0.ani in ('列表1', '列表2') or
       '0' || t0.ani in ('列表1', '列表2', '列表3')) and
       (t0.acdgroup in ('列表4')))
 group by t0.ani;

select t0.ani as col_0_0_, count(t0.id) as col_1_0_
  from t0 t0
  left join (select t1.id
               from t1
              where t1.begin_date >=
                    to_date('2014-08-08 00:00:00', 'yyyy-MM-dd HH24:mi:ss')
                and t1.begin_date <=
                    to_date('2014-08-08 23:59:59', 'yyyy-MM-dd HH24:mi:ss')) t1
    on (t1.id = t0.lead_interaction_id)
 where (t0.task_id is not null)
   and ((t0.type > 1 or t0.type < 1) and
       t0.dbdt < current_timetamp - (8 / 24) and
       (t0.ani in ('列表1') or concat('0', t0.ani) in ('列表1')) or
       t0.type = 1 and t1.id is not null and
       (t0.ani in ('列表1', '列表2') or
       '0' || t0.ani in ('列表1', '列表2', '列表3')) and
       (t0.acdgroup in ('列表4')))
 group by t0.ani;
