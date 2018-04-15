/*SYSDATE能得到的信息*/
select hiredate as 雇佣日期,
       to_date(to_char(hiredate, 'yyyy-mm') || '-1', 'yyyy-mm-dd') as 月初
  from emp
 where rownum <= 1;

select hiredate as 雇佣日期, trunc(hiredate, 'mm') as 月初
  from emp
 where rownum <= 1;

select hiredate,
       to_number(to_char(hiredate, 'hh24')) 时,
       to_number(to_char(hiredate, 'mi')) 分,
       to_number(to_char(hiredate, 'ss')) 秒,
       to_number(to_char(hiredate, 'dd')) 日,
       to_number(to_char(hiredate, 'mm')) 月,
       to_number(to_char(hiredate, 'yyyy')) 年,
       to_number(to_char(hiredate, 'ddd')) 年内第几天,
       trunc(hiredate, 'dd') 一天开始,
       trunc(hiredate, 'day') 周初,
       trunc(hiredate, 'mm') 月初,
       last_day(hiredate) 月未,
       add_months(trunc(hiredate, 'mm'), 1) 下月初,
       trunc(hiredate, 'yy') 年初,
       to_char(hiredate, 'day') 周几,
       to_char(hiredate, 'month') 月份
  from (select hiredate + 30 / 24 / 60 / 60 + 20 / 24 / 60 + 5 / 24 as hiredate
          from emp
         where rownum <= 1);

with t as
 (select to_date('1980-12-31 15:20:30', 'yyyy-mm-dd hh24:mi:ss') as d1,
         to_date('1980-12-31 05:20:30', 'yyyy-mm-dd hh24:mi:ss') as d2
    from dual)
select d1, d2 from t;

with t as
 (select to_date('1980-12-31 15:20:30', 'yyyy-mm-dd hh24:mi:ss') as d1,
         to_date('1980-12-31 05:20:30', 'yyyy-mm-dd hh24:mi:ss') as d2
    from dual)
select d1, d2 from t where d1 between trunc(d2, 'mm') and last_day(d2);

with t as
 (select to_date('1980-12-31 15:20:30', 'yyyy-mm-dd hh24:mi:ss') as d1,
         to_date('1980-12-31 05:20:30', 'yyyy-mm-dd hh24:mi:ss') as d2
    from dual)
select d1, d2
  from t
 where d1 >= trunc(d2, 'mm')
   and d1 < add_months(trunc(d2, 'mm'), 1);
/*INTERVAL*/
select interval '2' year as "year",
       interval '50' month as "month",
       interval '99' day as "day", /*最大只能用99*/
       interval '80' hour as "hour",
       interval '90' minute as "minute",
       interval '3.15' second as "second",
       interval '2 12:30:59' day to second as "day to second",
       interval '13-3' year to month as "year to month"
  from dual;
/*EXTRACT*/
create table test as
  select extract(year from systimestamp) as "year",
         extract(month from systimestamp) as "month",
         extract(day from systimestamp) as "day",
         extract(hour from systimestamp) as "hour",
         extract(minute from systimestamp) as "minute",
         extract(second from systimestamp) as "second"
    from dual;
select * from test;

select created, extract(day from created) as d
  from dba_objects
 where object_id = 2;

select extract(hour from created) as h
  from dba_objects
 where object_id = 2;

select created, to_char(created, 'dd') as d, to_char(created, 'hh24') as h
  from dba_objects
 where object_id = 2;

select extract(hour from it) as "hour"
  from (select interval '2 12:30:59' day to second as it from dual);

select to_char(it, 'hh24') as "hour"
  from (select interval '2 12:30:59' day to second as it from dual);
/*确定一年是否是为闰年*/
select trunc(hiredate, 'y') 年初 from emp where empno = 7788;

select add_months(trunc(hiredate, 'y'), 1) 二月初
  from emp
 where empno = 7788;

select last_day(add_months(trunc(hiredate, 'y'), 1)) as 二月底
  from emp
 where empno = 7788;

select to_char(last_day(add_months(trunc(hiredate, 'y'), 1)), 'DD') as 日
  from emp
 where empno = 7788;
/*周的计算*/
with x as
 (select trunc(sysdate, 'YY') + (level - 1) as 日期
    from dual
  connect by level <= 8)
select 日期,
       /*返回值1代表周日,2代表周一....*/
       to_char(日期, 'd') as d,
       to_char(日期, 'day') as day,
       /*参数2中1代表周日,2代表周一....*/
       next_day(日期, 1) as 下个周日,
       /*ww的算法为每年1月1日为周一开始,date+6为每周一结束*/
       to_char(日期, 'ww') as ww,
       /*iw的算法为周一至星期日算一周,且每年的第一个星期一为第一周*/
       to_char(日期, 'iw') as iw
  from x;
/*确定一年内属于周内某一天的所有日期*/
with x as
 (select trunc(sysdate, 'y') + (level - 1) dy
    from dual
  connect by level <=
             add_months(trunc(sysdate, 'y'), 12) - trunc(sysdate, 'y'))
select dy, to_char(dy, 'day') as 周五 from x where to_char(dy, 'd') = 6;

select to_char(hiredate, 'day') as day, to_char(hiredate, 'd') as d
  from emp
 where rownum <= 1;
/*确定某月内第一个和最后一个“周内某天”的日期*/
select next_day(trunc(hiredate, 'mm') - 1, 2) 第一个周一,
       next_day(last_day(trunc(hiredate, 'mm')) - 7, 2) 最后一个周一
  from emp
 where empno = 7788;

with x as
 (select to_date('2013-03-24', 'yyyy-mm-dd') + (level - 1) as dy
    from dual
  connect by level <= 10)
select dy, to_char(dy, 'day') as day, next_day(dy, 2) as d1 from x;

with x as
 (select to_date('2013-09-23', 'yyyy-mm-dd') + (level - 1) as dy
    from dual
  connect by level <= 15)
select dy, to_char(dy, 'day') as day, next_day(dy, 2) as d1 from x;
/*创建本月日历*/
with x1 as
/*1、给定一个日期*/
 (select to_date('2013-03-01', 'yyyy-mm-dd') as cur_date from dual),
x2 as
/*2、取月初*/
 (select trunc(cur_date, 'mm') as 月初,
         add_months(trunc(cur_date, 'mm'), 1) as 下月初
    from x1),
x3 as
/*3、枚举当月所有的天*/
 (select 月初 + (level - 1) as 日
    from x2
  connect by level <= (下月初 - 月初)),
x4 as
/*4、提取周信息*/
 (select to_char(日, 'iw') 所在周,
         to_char(日, 'dd') 日期,
         to_number(to_char(日, 'd')) 周几
    from x3)
select max(case 周几
             when 2 then
              日期
           end) 周一,
       max(case 周几
             when 3 then
              日期
           end) 周二,
       max(case 周几
             when 4 then
              日期
           end) 周三,
       max(case 周几
             when 5 then
              日期
           end) 周四,
       max(case 周几
             when 6 then
              日期
           end) 周五,
       max(case 周几
             when 7 then
              日期
           end) 周六,
       max(case 周几
             when 1 then
              日期
           end) 周日
  from x4
 group by 所在周
 order by 所在周;
/*全年日历*/
with x as
 (select to_date('2013-12-27', 'yyyy-mm-dd') + (level - 1) as d
    from dual
  connect by level <= 5)
select d, to_char(d, 'day') as day, to_char(d, 'iw') as iw from x;

with x0 as
 (select to_date('2013-12-27', 'yyyy-mm-dd') + (level - 1) as d
    from dual
  connect by level <= 5),
x1 as
 (select d,
         to_char(d, 'day') as day,
         to_char(d, 'mm') as mm,
         to_char(d, 'iw') as iw
    from x0)
select d,
       day,
       case
         when mm = '12' and iw = '01' then
          '53'
         else
          iw
       end as iw
  from x1;

with x0 as
 (select 2013 as 年份 from dual),
x1 as
 (select trunc(to_date(年份, 'yyyy'), 'YYYY') as 本年初,
         add_months(trunc(to_date(年份, 'yyyy'), 'YYYY'), 12) as 下年初
    from x0),
x2 as
/*枚举日期*/
 (select 本年初 + (level - 1) as 日期
    from x1
  connect by level <= 下年初 - 本年初),
x3 as
 (
  /*取月份，及周信息*/
  select 日期,
          to_char(日期, 'mm') 所在月份,
          to_char(日期, 'iw') 所在周,
          to_number(to_char(日期, 'd')) 周几
    from x2),
x4 as
/*修正周,12月的“第一周”改为“第十三周”*/
 (select 日期,
         所在月份,
         case
           when 所在月份 = '12' and 所在周 = '01' then
            '53'
           else
            所在周
         end as 所在周,
         周几
    from x3)
select case
         when lag(所在月份) over(order by 所在周) = 所在月份 then
          null
         else
          所在月份
       end as 月份,
       所在周,
       max(case 周几
             when 2 then
              日期
           end) 周一,
       max(case 周几
             when 3 then
              日期
           end) 周二,
       max(case 周几
             when 4 then
              日期
           end) 周三,
       max(case 周几
             when 5 then
              日期
           end) 周四,
       max(case 周几
             when 6 then
              日期
           end) 周五,
       max(case 周几
             when 7 then
              日期
           end) 周六,
       max(case 周几
             when 1 then
              日期
           end) 周日
  from x4
 group by 所在月份, 所在周
 order by 2;
/*确定指定年份季度的开始日期和结束日期*/
select sn as 季度,
       (sn - 1) * 3 + 1 as 开始月份,
       add_months(to_date(年, 'yyyy'), (sn - 1) * 3) as 开始日期,
       add_months(to_date(年, 'yyyy'), sn * 3) - 1 as 结束日期
  from (select '2013' as 年, level as sn from dual connect by level <= 4);
/*补充范围内丢失的值*/
select empno, hiredate from scott.emp order by 2;

select to_char(hiredate, 'yyyy') as year, count(*) as cnt
  from scott.emp
 group by to_char(hiredate, 'yyyy')
 order by 1;

with x as
 (select 开始年份 + (level - 1) as 年份
    from (select extract(year from min(hiredate)) as 开始年份,
                 extract(year from max(hiredate)) as 结束年份
            from scott.emp)
  connect by level <= 结束年份 - 开始年份 + 1)
select * from x;

with x as
 (select 开始年份 + (level - 1) as 年份
    from (select extract(year from min(hiredate)) as 开始年份,
                 extract(year from max(hiredate)) as 结束年份
            from scott.emp)
  connect by level <= 结束年份 - 开始年份 + 1)
select x.年份, count(e.empno) 聘用人数
  from x
  left join scott.emp e
    on (extract(year from e.hiredate) = x.年份)
 group by x.年份
 order by 1;
/*按照给定的时间单位进行查找*/
select ename 姓名, hiredate 聘用日期, to_char(hiredate, 'day') as 星期
  from emp
 where to_char(hiredate, 'mm') in ('02', '12')
    or to_char(hiredate, 'd') = '3';
/*使用日期的特殊部分比较记录*/
select ename as 姓名,
       hiredate as 聘用日期,
       to_char(hiredate, 'mon day') as 月周
  from (select ename,
               hiredate,
               count(*) over(partition by to_char(hiredate, 'mon day')) as ct
          from emp)
 where ct > 1;
/*识别重叠的日期范围*/
create or replace view emp_project(empno, ename, proj_id, proj_start, proj_end) as
  select 7782, 'CLARK', 1, date '2005-06-16', date '2005-06-18'
    from dual
  union all
  select 7782, 'CLARK', 4, date '2005-06-19', date '2005-06-24'
    from dual
  union all
  select 7782, 'CLARK', 7, date '2005-06-22', date '2005-06-25'
    from dual
  union all
  select 7782, 'CLARK', 10, date '2005-06-25', date '2005-06-28'
    from dual
  union all
  select 7782, 'CLARK', 13, date '2005-06-28', date '2005-07-02'
    from dual
  union all
  select 7839, 'KING', 2, date '2005-06-17', date '2005-06-21'
    from dual
  union all
  select 7839, 'KING', 2, date '2005-06-17', date '2005-06-21'
    from dual
  union all
  select 7839, 'KING', 8, date '2005-06-23', date '2005-06-25'
    from dual
  union all
  select 7839, 'KING', 14, date '2005-06-29', date '2005-06-30'
    from dual
  union all
  select 7839, 'KING', 11, date '2005-06-26', date '2005-06-27'
    from dual
  union all
  select 7839, 'KING', 5, date '2005-06-20', date '2005-06-24'
    from dual
  union all
  select 7934, 'MILLER', 3, date '2005-06-18', date '2005-06-22'
    from dual
  union all
  select 7934, 'MILLER', 12, date '2005-06-27', date '2005-06-28 '
    from dual
  union all
  select 7934, 'MILLER', 15, date '2005-06-30', date '2005-07-03'
    from dual
  union all
  select 7934, 'MILLER', 9, date '2005-06-24', date '2005-06-27'
    from dual
  union all
  select 7934, 'MILLER', 6, date '2005-06-21', date '2005-06-23'
    from dual;

select empno as 员工编码,
       ename as 姓名,
       proj_id as 工程号,
       proj_start as 开始日期,
       lag(proj_end) over(partition by empno order by proj_start) as 上一工程结束日期,
       proj_end as 结束日期,
       lag(proj_id) over(partition by empno order by proj_start) as 上一工程号
  from emp_project;

select a.员工编码,
       a.姓名,
       a.工程号,
       a.开始日期,
       a.结束日期,
       case
         when 上一工程结束日期 >= 开始日期 /*筛选时间重复的数据*/
          then
          '(工程' || lpad(a.工程号, 2, '0') || ')与工程(' ||
          lpad(a.上一工程号, 2, '0') || ')重复'
       end as 描述
  from (select empno as 员工编码,
               ename as 姓名,
               proj_id as 工程号,
               proj_start as 开始日期,
               proj_end as 结束日期,
               lag(proj_end) over(partition by empno order by proj_start) as 上一工程结束日期,
               lag(proj_id) over(partition by empno order by proj_start) as 上一工程号
          from emp_project) a
--where 上一工程结束日期 >= 开始日期
 order by 1, 4;
/*按指定间隔汇总数据(切换到system用户)*/
select timestamp, action, action_name from dba_audit_trail;

select trunc(sysdate, 'mi') as mi, trunc(sysdate, 'hh') as hh from dual;

with x0 as
 (select to_date('2013-12-18 16:15:15', 'yyyy-mm-dd hh24:mi:ss') as c1
    from dual)
select trunc(c1, 'mi') as c1, to_char(c1, 'mi') as mi from x0;

select mod(15, 10) as m from dual;

with x0 as
 (select to_date('2013-12-18 16:15:15', 'yyyy-mm-dd hh24:mi:ss') as c1
    from dual)
select trunc(c1, 'mi') - 5 / 24 / 60 from x0;

select gp, count(*) as cnt
  from (select timestamp,
               trunc(timestamp, 'mi') -
               mod(to_char(timestamp, 'mi'), 10) / 24 / 60 as gp,
               action,
               action_name
          from t_trail)
 group by gp;
