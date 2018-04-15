/*加减日、月、年*/
select hiredate as 聘用日期,
       hiredate - 5 as 减5天,
       hiredate + 5 as 加5天,
       add_months(hiredate, -5) as 减5个月,
       add_months(hiredate, 5) as 加5个月,
       add_months(hiredate, -5 * 12) as 减5年,
       add_months(hiredate, 5 * 12) as 加5年
  from emp
 where rownum <= 1;
/*加减时、分、秒*/
select hiredate as 聘用日期,
       hiredate - 5 / 24 / 60 / 60 as 减5秒,
       hiredate + 5 / 24 / 60 / 60 as 加5秒,
       hiredate - 5 / 24 / 60 as 减5分钟,
       hiredate + 5 / 24 / 60 as 加5分钟,
       hiredate - 5 / 24 as 减5小时,
       hiredate + 5 / 24 as 加5小时
  from emp
 where rownum <= 1;
/*日期间隔之时、分、秒*/
select 间隔天数,
       间隔天数 * 24 as 间隔小时,
       间隔天数 * 24 * 60 as 间隔分,
       间隔天数 * 24 * 60 * 60 as 间隔秒
  from (select max(hiredate) - min(hiredate) as 间隔天数
          from emp
         where ename in ('WARD', 'ALLEN')) x;
/*日期间隔之日、月、年*/
select max_hd - min_hd 间隔天,
       months_between(max_hd, min_hd) 间隔月,
       months_between(max_hd, min_hd) / 12 间隔年
  from (select min(hiredate) min_hd, max(hiredate) max_hd from emp) x;
/*确定两个日期之间的工作天数*/
create table t500 as select level as id from dual connect by level <=500;

select sum(case
             when to_char(min_hd + t500.id - 1,
                          'DY',
                          'NLS_DATE_LANGUAGE= American') in ('SAT', 'SUN') then
              0
             else
              1
           end) as工作天数
  from (select min(hiredate) as min_hd, max(hiredate) as max_hd
          from emp
         where ename in ('BLAKE', 'JONES')) x,
       t500
 where t500.id <= max_hd - min_hd + 1;

select ename, hiredate from emp where ename in ('BLAKE', 'JONES');

select min(hiredate) as min_hd, max(hiredate) as max_hd
  from emp
 where ename in ('BLAKE', 'JONES');

select (max_hd - min_hd) + 1 as 天数
  from (select min(hiredate) as min_hd, max(hiredate) as max_hd
          from emp
         where ename in ('BLAKE', 'JONES')) x;

select min_hd + (t500.id - 1) as 日期
  from (select min(hiredate) as min_hd, max(hiredate) as max_hd
          from emp
         where ename in ('BLAKE', 'JONES')) x,
       t500
 where t500.id <= ((max_hd - min_hd) + 1);

select 日期, to_char(日期, 'DY', 'NLS_DATE_LANGUAGE = American') as dy
  from (select min_hd + (t500.id - 1) as 日期
          from (select min(hiredate) as min_hd, max(hiredate) as max_hd
                  from emp
                 where ename in ('BLAKE', 'JONES')) x,
               t500
         where t500.id <= ((max_hd - min_hd) + 1));

select count(*)
  from (select 日期,
               to_char(日期, 'DY', 'NLS_DATE_LANGUAGE = American') as dy
          from (select min_hd + (t500.id - 1) as 日期
                  from (select min(hiredate) as min_hd,
                               max(hiredate) as max_hd
                          from emp
                         where ename in ('BLAKE', 'JONES')) x,
                       t500
                 where t500.id <= ((max_hd - min_hd) + 1)))
 where dy not in ('SAT', 'SUN');
/*计算一年中周内各日期的次数*/
with x0 as
 (select to_date('2013-01-01', 'yyyy-mm-dd') as 年初 from dual),
x1 as
 (select 年初, add_months(年初, 12) as 下年初 from x0),
x2 as
 (select 年初, 下年初, 下年初 - 年初 as 天数 from x1),
x3 as /*生成列表*/
 (select 年初 + (level + 1) as 日期 from x2 connect by level <= 天数),
x4 as /*对数据进行转换*/
 (select 日期, to_char(日期, 'DY') as 星期 from x3)
/*汇总求天数*/
select 星期, count(*) as 天数 from x4 group by 星期;

with x0 as
 (select to_date('2013-01-01', 'yyyy-mm-dd') as 年初 from dual),
x1 as
 (select 年初, add_months(年初, 12) as 年底 from x0),
x2 as
 (select next_day(年初 - 1, level) as d1, next_day(年底 - 8, level) as d2
    from x1
  connect by level <= 7)
select to_char(d1, 'dy') as 星期, d1, d2 from x2;

with x0 as
 (select to_date('2013-01-01', 'yyyy-mm-dd') as 年初 from dual),
x1 as
 (select 年初, add_months(年初, 12) as 年底 from x0),
x2 as
 (select next_day(年初 - 1, level) as d1, next_day(年底 - 8, level) as d2
    from x1
  connect by level <= 7)
select to_char(d1, 'dy') as 星期, ((d2 - d1) / 7 + 1) as 天数
  from x2
 order by 1;
/*确定当前记录和下一条记录之间相差的天数*/
select deptno,
       ename,
       hiredate,
       lead(hiredate) over(order by hiredate) next_hd
  from emp
 where deptno = 10;

select ename, hiredate, next_hd, next_hd - hiredate diff
  from (select deptno,
               ename,
               hiredate,
               lead(hiredate) over(order by hiredate) next_hd
          from emp
         where deptno = 10);

select ename, hiredate, next_hd, next_hd - hiredate diff
  from (select deptno,
               ename,
               hiredate,
               lead(hiredate) over(order by hiredate) next_hd
          from emp
         where deptno = 10);

select deptno,
       ename,
       hiredate,
       lead(hiredate) over(order by hiredate) next_hd,
       hiredate,
       lag(hiredate) over(order by hiredate) lag_hd
  from emp
 where deptno = 10;
