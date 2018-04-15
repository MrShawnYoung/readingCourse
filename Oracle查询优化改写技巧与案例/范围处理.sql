/*定位连续值的范围*/
create or replace view v(proj_id,proj_start,proj_end) as
select 1, date '2005-01-01', date '2005-01-02'
  from dual
union all
select 2, date '2005-01-02', date '2005-01-03'
  from dual
union all
select 3, date '2005-01-03', date '2005-01-04'
  from dual
union all
select 4, date '2005-01-04', date '2005-01-05'
  from dual
union all
select 5, date '2005-01-06', date '2005-01-07'
  from dual
union all
select 6, date '2005-01-16', date '2005-01-17'
  from dual
union all
select 7, date '2005-01-17', date '2005-01-18'
  from dual
union all
select 8, date '2005-01-18', date '2005-01-19'
  from dual
union all
select 9, date '2005-01-19', date '2005-01-20'
  from dual
union all
select 10, date '2005-01-21', date '2005-01-22'
  from dual
union all
select 11, date '2005-01-26', date '2005-01-27'
  from dual
union all
select 12, date '2005-01-27', date '2005-01-28'
  from dual
union all
select 13, date '2005-01-28', date '2005-01-29'
  from dual
union all
select 14, date '2005-01-29', date '2005-01-30'
  from dual;

select v1.proj_id as 工程号, v1.proj_start 开始日期, v1.proj_end 结束日期
  from v v1, v v2
 where v2.proj_start = v1.proj_end;

select proj_id as 工程号,
       proj_start 开始日期,
       proj_end 结束日期,
       lead(proj_start) over(order by proj_id) 下一个工程开始日期
  from v;

select 工程号, 开始日期, 结束日期
  from (select proj_id as 工程号,
               proj_start 开始日期,
               proj_end 结束日期,
               lead(proj_start) over(order by proj_id) 下一个工程开始日期
          from v)
 where 下一个工程开始日期 = 结束日期;
/*查找同一组或分区中行之间的差*/
select * from log;

with x0 as
 (select rownum as seq, 登录名, 登录时间
    from (select 登录名, 登录时间 from log order by 登录名, 登录时间) e)
select * from x0;

with x0 as
 (select rownum as seq, 登录名, 登录时间
    from (select 登录名, 登录时间 from log order by 登录名, 登录时间) e)
select e1.登录名, e1.登录时间, e2.登录时间 as 下一登录时间
  from x0 e1
  left join x0 e2
    on (e2.登录名 = e1.登录名 and e2.seq = e1.seq + 1)
 order by 1, 2;

select 登录名,
       登录时间,
       lead(登录时间) over(partition by 登录名 order by 登录时间) as 下一登录时间
  from log;

select 登录名, 登录时间, (下一登录时间 - 登录时间) * 24 * 60 as 登录间隔
  from (select 登录名,
               登录时间,
               lead(登录时间) over(partition by 登录名 order by 登录时间) as 下一登录时间
          from log);
/*定位连续值范围的开始点和结束点*/
select min(proj_start) as 开始, max(proj_end) as 结束 from v;

create or replace view x0 as
select proj_id as 编号,
       proj_start as 开始日期,
       proj_end as 结束日期,
       lag(proj_end) over(order by proj_id) as 上一工程结束日期
  from v;
select * from x0;

create or replace view x1 as
select 编号,
       开始日期,
       结束日期,
       上一工程结束日期,
       case
         when 开始日期 = 上一工程结束日期 then
          0
         else
          1
       end as 连续状态
  from x0;
select * from x1;

create or replace view x2 as
select 编号,
       开始日期,
       结束日期,
       上一工程结束日期,
       连续状态,
       sum(连续状态) over(order by 编号) as 分组依据
  from x1;
select * from x2;

select 分组依据, min(开始日期) as 开始日期, max(结束日期) as 结束日期
  from (select 编号,
               开始日期,
               结束日期,
               sum(连续状态) over(order by 编号) as 分组依据
          from (select proj_id as 编号,
                       proj_start as 开始日期,
                       proj_end as 结束日期,
                       case
                         when lag(proj_end)
                          over(order by proj_id) = proj_start then
                          0
                         else
                          1
                       end as 连续状态
                  from v))
 group by 分组依据
 order by 1;
/*合并时间段*/
create or replace view timesheets(task_id,start_date,end_date) as
select 1, date '1997-01-01', date '1997-01-03'
  from dual
union all
select 2, date '1997-01-02', date '1997-01-04'
  from dual
union all
select 3, date '1997-01-04', date '1997-01-05'
  from dual
union all
select 4, date '1997-01-06', date '1997-01-09'
  from dual
union all
select 5, date '1997-01-09', date '1997-01-09'
  from dual
union all
select 6, date '1997-01-09', date '1997-01-09'
  from dual
union all
select 7, date '1997-01-12', date '1997-01-15'
  from dual
union all
select 8, date '1997-01-13', date '1997-01-13'
  from dual
union all
select 9, date '1997-01-15', date '1997-01-15'
  from dual
union all
select 10, date '1997-01-17', date '1997-01-17'
  from dual;

select 分组依据, min(开始日期) as 开始日期, max(结束日期) as 结束日期
  from (select 编号,
               开始日期,
               结束日期,
               sum(连续状态) over(order by 编号) as 分组依据
          from (select task_id as 编号,
                       start_date as 开始日期,
                       end_date as 结束日期,
                       case
                         when lag(end_date)
                          over(order by task_id) >= start_date then
                          0
                         else
                          1
                       end as 连续状态
                  from timesheets))
 group by 分组依据
 order by 1;

select start_date,
       end_date,
       max(end_date) over(order by start_date rows between unbounded preceding and 1 preceding) as max_end_date
  from timesheets b;

with x0 as
 (select task_id,
         start_date,
         end_date,
         max(end_date) over(order by start_date rows between unbounded preceding and 1 preceding) as max_end_date
    from timesheets b),
x1 as
 (select start_date as 开始时间,
         end_date as 结束时间,
         max_end_date,
         case
           when max_end_date >= start_date then
            0
           else
            1
         end as 连续状态
    from x0)
select * from x1;

with x0 as
 (select task_id,
         start_date,
         end_date,
         max(end_date) over(order by start_date rows between unbounded preceding and 1 preceding) as max_end_date
    from timesheets b),
x1 as
 (select start_date as 开始时间,
         end_date as 结束时间,
         max_end_date,
         case
           when max_end_date >= start_date then
            0
           else
            1
         end as 连续状态
    from x0),
x2 as
 (select 开始时间,
         结束时间,
         sum(连续状态) over(order by 开始时间) as 分组依据
    from x1)
select 分组依据, min(开始时间) as 开始时间, max(结束时间) as 结束时间
  from x2
 group by 分组依据
 order by 分组依据;
