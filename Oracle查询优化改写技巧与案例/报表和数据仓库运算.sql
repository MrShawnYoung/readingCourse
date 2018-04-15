/*行转列*/
select job as 工作,
       case deptno
         when 10 then
          sal
       end as 部门10工资,
       case deptno
         when 20 then
          sal
       end as 部门20工资,
       case deptno
         when 30 then
          sal
       end as 部门30工资,
       sal as 合计工资
  from emp
 order by 1;

select job as 工作,
       sum(case deptno
             when 10 then
              sal
           end) as 部门10工资,
       sum(case deptno
             when 20 then
              sal
           end) as 部门20工资,
       sum(case deptno
             when 30 then
              sal
           end) as 部门30工资,
       sum(sal) as 合计工资
  from emp
 group by job
 order by 1;

select *
  from (select job, /*该列未在pivot中，所以被当作分组条件*/ sal, deptno
          from emp)
pivot(sum(sal) as s /*SUM、MAX等聚集函数+列别名，若不设置，则默认只使用后面in里设的别名，否则两个别名相加*/
   for deptno in(10 as d10, /*相当于sum(case when deptno = 10 then sal end) as
                 d10别名与前面的别名合并后为D10_s*/
                 20, /*相当于sum(case when deptno = 20 then sal end) as 20
                 若列别名不设置，则默认使用值作为别名，此处为20，与前面的合并后为20_s*/
                 30 as d30 /*相当于sum(case when deptno = 30 then sal end) as d30
                 别名与前面的别名合并后为D30_S*/))
 order by 1;

select *
  from (select job, sal, comm, deptno from emp)
pivot(sum(sal) as s, sum(comm) as c
   for deptno in(10 as d10, 20 as d20, 30 as d30))
 order by 1;

select job,
       sum(case
             when deptno = 10 then
              sal
           end) as d10_s,
       sum(case
             when deptno = 20 then
              sal
           end) as d20_s,
       sum(case
             when deptno = 30 then
              sal
           end) as d30_s,
       sum(case
             when deptno = 10 then
              comm
           end) as d10_c,
       sum(case
             when deptno = 20 then
              comm
           end) as d20_c,
       sum(case
             when deptno = 30 then
              comm
           end) as d30_c
  from emp
 group by job
 order by 1;

select count(case
               when deptno = 10 then
                ename
             end) as deptno_10,
       count(case
               when deptno = 20 then
                ename
             end) as deptno_10,
       count(case
               when deptno = 30 then
                ename
             end) as deptno_10,
       count(case
               when job = 'CLERK' then
                comm
             end) as clerks,
       count(case
               when job = 'MANAGER' then
                comm
             end) as mgrs,
       count(case
               when job = 'PRESIDENT' then
                comm
             end) as prez,
       count(case
               when job = 'ANALYST' then
                comm
             end) as anals,
       count(case
               when job = 'SALESMAN' then
                comm
             end) as sales
  from emp;
/*列转行*/
create table test as
select *
  from (select deptno, sal from emp)
pivot(count(*) as ct, sum(sal) as s
   for deptno in(10 as deptno_10, 20 as deptno_20, 30 as deptno_30));

select '10' as 部门编码, deptno_10_ct as 人次
  from test
union all
select '20' as 部门编码, deptno_20_ct as 人次
  from test
union all
select '30' as 部门编码, deptno_30_ct as 人次
  from test;

select *
  from test unpivot(人次 for deptno in(deptno_10_ct,
                                     deptno_20_ct,
                                     deptno_30_ct));

select deptno as 列名, substr(deptno, -5, 2) as 部门编码, 人次
  from test unpivot(人次 for deptno in(deptno_10_ct,
                                     deptno_20_ct,
                                     deptno_30_ct));

select a.列名, a.部门编码, a.人次, b.工资
  from (select substr(deptno, 1, 9) as 列名,
               substr(deptno, -5, 2) as 部门编码,
               人次
          from test unpivot include nulls(人次 for deptno in(deptno_10_ct,
                                                           deptno_20_ct,
                                                           deptno_30_ct))) a
 inner join (select substr(deptno, 1, 9) as 列名, 工资
               from test unpivot include nulls(工资 for deptno in(deptno_10_s,
                                                                deptno_20_s,
                                                                deptno_30_s))) b
    on (b.列名 = a.列名);

select deptno, 人次, deptno2, 工资
  from test unpivot include nulls(人次 for deptno in(deptno_10_ct as 10,
                                                   deptno_20_ct as 20,
                                                   deptno_30_ct as 30)) unpivot include nulls(工资 for deptno2 in(deptno_10_s as 10,
                                                                                                                deptno_20_s as 20,
                                                                                                                deptno_30_s as 30))
 order by 1, 3;

with x0 as
 (select deptno, 人次, deptno_10_s, deptno_20_s, deptno_30_s
    from test unpivot include nulls(人次 for deptno in(deptno_10_ct as 10,
                                                     deptno_20_ct as 20,
                                                     deptno_30_ct as 30)))
select deptno, 人次, deptno2, 工资
  from x0 unpivot include nulls(工资 for deptno2 in(deptno_10_s as 10,
                                                  deptno_20_s as 20,
                                                  deptno_30_s as 30))
 order by 1, 3;

select deptno as 部门编码, 人次, 工资
  from test unpivot include nulls(人次 for deptno in(deptno_10_ct as 10,
                                                   deptno_20_ct as 20,
                                                   deptno_30_ct as 30)) unpivot include nulls(工资 for deptno2 in(deptno_10_s as 10,
                                                                                                                deptno_20_s as 20,
                                                                                                                deptno_30_s as 30))
 where deptno = deptno2;
/*将结果集反向转置为一列*/
select emps
  from (select ename, job, to_char(sal) as sal, null as t_col /*增加这一列来显示空行*/
          from emp
         where deptno = 10) unpivot include nulls(emps for col in(ename,
                                                                  job,
                                                                  sal,
                                                                  t_col));

select emps
  from (select ename, job, sal, null as t_col /*增加这一列来显示空行*/
          from emp
         where deptno = 10) unpivot include nulls(emps for col in(ename,
                                                                  job,
                                                                  sal,
                                                                  t_col));

select emps
  from (select ename, job, to_char(sal) as sal, null as t_col
          from emp
         where deptno = 10) unpivot(emps for col in(ename, job, sal, t_col));
/*抑制结果集中的重复值*/
select case
       /*当部门分类按姓名排序后与上一条内容相同时不显示*/
         when lag(job) over(order by job, ename) = job then
          null
         else
          job
       end as 职位,
       ename as 姓名
  from emp
 where deptno = 20
 order by emp.job, ename;

select case
         when lag(job) over(order by job, ename) = job then
          null
         else
          job
       end as job,
       ename as 姓名
  from emp
 where deptno = 20
 order by job, ename;
/*利用“行转列”进行计算*/
select d10_sal,
       d20_sal,
       d30_sal,
       d20_sal - d10_sal as d20_10_diff,
       d20_sal - d30_sal as d20_30_diff
  from (select sum(case
                     when deptno = 10 then
                      sal
                   end) as d10_sal,
               sum(case
                     when deptno = 20 then
                      sal
                   end) as d20_sal,
               sum(case
                     when deptno = 30 then
                      sal
                   end) as d30_sal
          from emp) totals_by_dept;
/*给数据分组*/
with x1 as
 (select ename from emp order by ename),
x2 as
 (select rownum as rn, ename from x1)
select * from x2;

with x1 as
 (select ename from emp order by ename),
x2 as
 (select rownum as rn, ename from x1),
x3 as
 (select ceil(rn / 5) as gp, ename from x2)
select * from x3;

with x1 as
 (select ename from emp order by ename),
x2 as
 (select rownum as rn, ename from x1),
x3 as
 (select ceil(rn / 5) as gp, ename from x2),
x4 as
 (select gp, ename, row_number() over(partition by gp order by ename) as rn
    from x3)
select * from x4;

with x1 as
/*1.排序*/
 (select ename from emp order by ename),
x2 as
/*2.生成序号*/
 (select rownum as rn, ename from x1),
x3 as
/*3.分组*/
 (select ceil(rn / 5) as gp, ename from x2),
/*4.分组生成序号*/
x4 as
 (select gp, ename, row_number() over(partition by gp order by ename) as rn
    from x3)
/*5.行转列*/
select *
  from x4
pivot (max(ename) for rn in(1 as n1, 2 as n2, 3 as n3, 4 as n4, 5 as n5));
/*对数据分组*/
select ntile(3) over(order by empno) as 组, empno as 编码, ename as 姓名
  from emp
 where job in ('CLERK', 'MANAGER');
/*计算简单的小计*/
select deptno, sum(sal) as s_sal from emp group by rollup(deptno);

select deptno as 部门编码, job as 工作, mgr as 主管, sum(sal) as s_sal
  from emp
 group by rollup(deptno, job, mgr);

select deptno as 部门编码, job as 工作, mgr as 主管, sum(sal) as s_sal
  from emp
 group by deptno, job, mgr
union all
select deptno as 部门编码,
       job as 工作,
       null /*工作小计*/ as 主管,
       sum(sal) as s_sal
  from emp
 group by deptno, job
union all
select deptno as 部门编码,
       null /*部门小计*/ as 工作,
       null as 主管,
       sum(sal) as s_sal
  from emp
 group by deptno
union all
select null /*总合计*/, null as 工作, null as 主管, sum(sal) as s_sal
  from emp;

select deptno as 部门编码, job 工作, sum(sal) as 工资小计
  from emp
 group by rollup((deptno, job));
/*判别非小计的行(不改原表复制一张表出来)*/
create table temp_emp as select * from emp;

update temp_emp set job = null where empno = 7788;

update temp_emp set deptno = null where empno in (7654, 7902);

select nvl(to_char(deptno), '总计') as 部门编码,
       nvl(job, '小计') as 工作,
       deptno,
       job,
       mgr as 主管,
       max(case
             when empno in (7788, 7654, 7902) then
              empno
           end) as max_empno,
       sum(sal) sal,
       grouping(deptno) deptno_grouping,
       grouping(job) job_grouping
  from temp_emp
 group by rollup(deptno, job, mgr);

select case grouping(deptno)
         when 1 then
          '总计'
         else
          to_char(deptno)
       end as 部门编码,
       case
         when grouping(deptno) = 1 then
          null
         when grouping(job) = 1 then
          '小计'
         else
          job
       end as 工作,
       case
         when grouping(job) = 1 then
          null
         when grouping(mgr) = 1 then
          '小计'
         else
          to_char(mgr)
       end as 主管,
       max(case
             when empno in (7788, 7654, 7902) then
              empno
           end) as max_empno,
       sum(sal) as 工资合计
  from temp_emp
 group by rollup(deptno, job, mgr);
/*计算所有表达式组合的小计*/
select case grouping(deptno) || grouping(job)
         when '00' then
          '按部门与工作分组'
         when '10' then
          '按工作分组'
         when '01' then
          '按部门分组'
         when '11' then
          '总合计'
       end as grouping,
       /*把grouping(deptno)||grouping(job)的结果当作二进制
       在转为十进制
       就是grouping_id(deptno,job)的值*/
       case grouping_id(deptno, job)
         when 0 then
          '按部门与工作分组'
         when 2 then
          '按工作分组'
         when 1 then
          '按部门分组'
         when 3 then
          '总合计'
       end grouping_id,
       deptno as 部门,
       job as 工作,
       sum(sal) as 工资
  from emp
 group by cube(deptno, job)
 order by grouping(job), grouping(deptno);
/*人员在工作间的分布*/
select *
  from (select ename, job from emp)
pivot(sum(1)
   for job in('CLERK' as is_clerk,
              'SALESMAN' as is_sales,
              'MANAGER' as is_mgr,
              'ANALYST' as is_analyst,
              'PRESIDENT' as is_prez))
 order by 2, 3, 4, 5, 6;
/*创建稀疏矩阵*/
select *
  from (select empno, ename, ename as ename2, deptno, job from emp)
pivot(max(ename)
   for deptno in(10 as deptno_10, 20 as deptno_20, 30 as deptno_30))
pivot(max(ename2)
   for job in('CLERK' as clerks,
              'MANAGER' as mgrs,
              'PRESIDENT' as prez,
              'ANALYST' as anals,
              'SALESMAN' as sales))
 order by 1;

select *
  from (select ename, ename as ename2, deptno, job from emp)
pivot(count(ename)
   for deptno in(10 as deptno_10, 20 as deptno_20, 30 as deptno_30))
pivot(count(ename2)
   for job in('CLERK' as clerks,
              'MANAGER' as mgrs,
              'PRESIDENT' as prez,
              'ANALYST' as anals,
              'SALESMAN' as sales))
 order by 1;

with x0 as
 (select *
    from (select ename, ename as ename2, deptno, job from emp)
  pivot(count(ename)
     for deptno in(10 as deptno_10, 20 as deptno_20, 30 as deptno_30)))
select * from x0;

with x0 as
 (select *
    from (select ename, ename as ename2, deptno, job from emp)
  pivot(count(ename)
     for deptno in(10 as deptno_10, 20 as deptno_20, 30 as deptno_30)))
select *
  from x0
pivot (count(ename2) for job in('CLERK' as clerks,
                           'MANAGER' as mgrs,
                           'PRESIDENT' as prez,
                           'ANALYST' as anals,
                           'SALESMAN' as sales))
 order by 1;
/*对不同组/分区同时实现聚集*/
select e.ename 姓名,
       e.deptno 部门,
       s_d.cnt as 部门人数,
       e.job as 职位,
       s_j.cnt as 职位人数,
       (select count(*) as cnt from emp where deptno in (10, 20)) as 总人数
  from emp e
 inner join (select deptno, count(*) as cnt
               from emp
              where deptno in (10, 20)
              group by deptno) s_d
    on (s_d.deptno = e.deptno)
 inner join (select job, count(*) as cnt
               from emp
              where deptno in (10, 20)
              group by job) s_j
    on (s_j.job = e.job)
 where e.deptno in (10, 20);

select ename 姓名,
       deptno 部门,
       count(*) over(partition by deptno) as 部门人数,
       job as 职位,
       count(*) over(partition by job) as 职位人数,
       count(*) over() as 总人数
  from emp
 where deptno in (10, 20);
/*对移动范围的值进行聚集*/
select hiredate as 聘用日期,
       sal as 工资,
       (select sum(b.sal)
          from emp b
         where b.deptno = 30
           and b.hiredate <= e.hiredate
           and b.hiredate >= e.hiredate - 90) as 标量求值,
       '(' || to_char(hiredate - 90, 'yy-mm-dd') || '到' ||
       to_char(hiredate, 'yy-mm-dd') || ')聘用人员工资和' as 需求,
       sum(sal) over(order by hiredate range between 90 preceding and current row) as 分析函数求值,
       (select listagg(b.sal, '+') within group(order by b.hiredate)
          from emp b
         where b.deptno = 30
           and b.hiredate <= e.hiredate
           and b.hiredate >= e.hiredate - 90) as 模拟公式
  from emp e
 where deptno = 30
 order by 1;

select hiredate as 聘用日期,
       sal as 工资,
       sum(sal) over(order by hiredate range between interval '3' month preceding and current row) as 仨月合计
  from emp e
 where deptno = 30
 order by 1;

create table test as
select 1 as c1, trunc(sysdate) + level / 24 / 60 as d1
  from dual
connect by level <= 5;

select * from test;

select c1,
       d1,
       sum(c1) over(order by d1 range between 2 / 24 / 60 preceding and current row) as s1,
       sum(c1) over(order by d1 range between(interval '2' minute) preceding and current row) as s2
  from test;
/*常用分析函数开窗讲解*/
select ename,
       sal,
       /*因是按工资排序，所以这个语句的结果就是所有行的最小值*/
       min(sal) over(order by sal) as min_11,
       /*上述语句默认参数如下，plan中可以看到*/
       min(sal) over(order by sal range between unbounded preceding and current row) as min_12,
       /*这种情况下，rows与range返回数据一样*/
       min(sal) over(order by sal rows between unbounded preceding and current row) as min_13,
       /*取所有行内最小值，可以与前面返回的值对比查看*/
       min(sal) over() as min_14,
       /*如果明确写出上面min_14的范围就是*/
       min(sal) over(order by sal range between unbounded preceding and unbounded following) as min_15,
       /*这种情况下，rows与range返回数据一样*/
       min(sal) over(order by sal rows between unbounded preceding and unbounded following) as min_16
  from emp
 where deptno = 30;

select ename,
       sal,
       /*因按工资排序，所以这个语句与上面sal返回的值一样*/
       max(sal) over(order by sal) as max_11,
       /*上述语句默认参数如下，plan中可以看到*/
       max(sal) over(order by sal range between unbounded preceding and current row) as max_12,
       /*这种情况下，rows与range返回数据一样*/
       max(sal) over(order by sal rows between unbounded preceding and current row) as max_13,
       /*取所有行内最大值，可以与前面返回的值对比查看*/
       max(sal) over() as max_14,
       /*如果明确写出上面min_14的范围就是*/
       max(sal) over(order by sal range between unbounded preceding and unbounded following) as max_15,
       /*这种情况下，rows与range返回数据一样*/
       max(sal) over(order by sal rows between unbounded preceding and unbounded following) as max_16
  from emp
 where deptno = 30;

select ename,
       sal,
       /*累加工资，要注意工资重复时的现象*/
       sum(sal) over(order by sal) as max_11,
       /*上述语句默认参数如下，plan中可以看到*/
       sum(sal) over(order by sal range between unbounded preceding and current row) as sum_12,
       /*这种情况下，rows与range返回数据不一样，见第二行*/
       sum(sal) over(order by sal rows between unbounded preceding and current row) as sum_13,
       /*工资合计*/
       sum(sal) over() as sum_14,
       /*如果明确写出上面sum_14的范围就是*/
       sum(sal) over(order by sal range between unbounded preceding and unbounded following) as sum_15,
       /*这种情况下，rows与range返回数据一样*/
       sum(sal) over(order by sal rows between unbounded preceding and unbounded following) as sum_16
  from emp
 where deptno = 30;

select ename,
       sal,
       /*当前行（+-1500）范围内的最大值*/
       max(sal) over(order by sal range between 500 preceding and 500 following) as max_11,
       /*前后一行，共三行中的最大值*/
       max(sal) over(order by sal rows between 1 preceding and 1 following) as max_12
  from emp
 where deptno = 30;
/*listagg与小九九*/
with l as
 (select level as lv from dual connect by level <= 9)
select a.lv as lv_a,
       b.lv as lv_b,
       to_char(b.lv) || ' × ' || to_char(a.lv) || ' = ' ||
       rpad(to_char(a.lv * b.lv), 2, ' ') as text
  from l a, l b
 where b.lv <= a.lv

with l as
 (select level as lv from dual connect by level <= 9),
m as
 (select a.lv as lv_a,
         b.lv as lv_b,
         to_char(b.lv) || ' × ' || to_char(a.lv) || ' = ' ||
         rpad(to_char(a.lv * b.lv), 2, ' ') as text
    from l a, l b
   where b.lv <= a.lv)
select listagg(m.text, '   ') within group(order by m.lv_b) as 小九九
  from m
 group by m.lv_a;
