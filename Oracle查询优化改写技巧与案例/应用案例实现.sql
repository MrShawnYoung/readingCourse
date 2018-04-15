/*从不固定位置提取字符串的元素*/
create or replace view v as
select 'xxxxxabc[867]xxx[-]xxxx[5309]xxxxx' msg
  from dual
union all
select 'xxxxxtime:[11271978]favnum:[4]id:[Joe]xxxxx' msg
  from dual
union all
select 'call:[F_GET_ROWS()]b1:[ROSEWOOD…SIR]b2:[44400002]77.90xxxxx' msg
  from dual
union all
select 'film:[non_marked]qq:[unit]tailpipe[withabanana?]80sxxxxx' msg
  from dual;

select regexp_substr(v.msg, '[^][]+', 1, 2) 第一个串,
       regexp_substr(v.msg, '[^][]+', 1, 4) 第二个串,
       regexp_substr(v.msg, '[^][]+', 1, 6) 第三个串,
       msg
  from v;

create or replace view v as
select 'xxxxxabc[867]xxx[-]xxxx[5309]xxxxx' msg
  from dual
union all
select 'xxxxxtime:[11271978]favnum:[4]id:[Joe]xxxxx' msg
  from dual
union all
select 'call:[F_GET_ROWS()]b1:[ROSEWOOD…SIR]b2:[44400002]77.90xxxxx' msg
  from dual
union all
select 'film:[non_marked]qq:[unit]tailpipe[withabanana?]80sxxxxx' msg
  from dual
union all
select '[一][二][三]' msg
  from dual;

select regexp_substr(v.msg, '(\[)([^]]+)', 1, 1) 第一个串,
       regexp_substr(v.msg, '(\[)([^]]+)', 1, 2) 第二个串,
       regexp_substr(v.msg, '(\[)([^]]+)', 1, 3) 第三个串,
       msg
  from v;

select ltrim(regexp_substr(v.msg, '(\[)([^]]+)', 1, 1), '[') 第一个串,
       ltrim(regexp_substr(v.msg, '(\[)([^]]+)', 1, 2), '[') 第二个串,
       ltrim(regexp_substr(v.msg, '(\[)([^]]+)', 1, 3), '[') 第三个串,
       msg
  from v;
/*搜索字母数字混合的字符串*/
create or replace view v as
select 'ClassSummary' strings
  from dual
union all
select '3453430278'
  from dual
union all
select 'findRow 55'
  from dual
union all
select '1010 switch'
  from dual
union all
select '333'
  from dual
union all
select 'threes'
  from dual;

select strings
  from v
 where regexp_like(v.strings, '([a-zA-Z].*[0-9]|[0-9].*[a-zA-Z])');
/*把结果分级并转为列*/
with x as
 (select ename as 姓名,
         sal as 工资,
         dense_rank() over(order by sal desc) as 档次
    from emp)
select * from x;

with x as
 (select ename as 姓名,
         sal as 工资,
         dense_rank() over(order by sal desc) as 档次
    from emp),
y as
 (select 姓名,
         工资,
         档次,
         case
           when 档次 <= 3 then
            1
           when 档次 <= 6 then
            2
           else
            3
         end 列
    from x)
select * from y;

with x as
 (select ename as 姓名,
         sal as 工资,
         dense_rank() over(order by sal desc) as 档次
    from emp),
y as
 (select 姓名,
         工资,
         档次,
         case
           when 档次 <= 3 then
            1
           when 档次 <= 6 then
            2
           else
            3
         end 列
    from x),
z as
 (select 姓名,
         工资,
         档次,
         列,
         row_number() over(partition by 列 order by 档次, 姓名) as 分组依据
    from y)
select * from z;

/*1.对数据分档*/
with x as
 (select ename as 姓名,
         sal as 工资,
         dense_rank() over(order by sal desc) as 档次
    from emp),
/*2.根据档次把数据分为三类*/
y as
 (select 姓名,
         工资,
         档次,
         case
           when 档次 <= 3 then
            1
           when 档次 <= 6 then
            2
           else
            3
         end 列
    from x),
/*3.分别对三列的数据重新取序号，这样相同序号的可以汇总后放在同一行*/
z as
 (select 姓名,
         工资,
         档次,
         列,
         row_number() over(partition by 列 order by 档次, 姓名) as 分组依据
    from y)
/*4.行转列*/
select max(case 列
             when 1 then
              rpad(姓名, 6) || '(' || 工资 || ')'
           end) 最高三档,
       max(case 列
             when 2 then
              rpad(姓名, 6) || '(' || 工资 || ')'
           end) 次级三档,
       max(case 列
             when 3 then
              rpad(姓名, 6) || '(' || 工资 || ')'
           end) 其余档次
  from z
 group by 分组依据
/*注意要排序，否则显示的数据是乱的*/
 order by 分组依据;
/*构建基础数据的重要性*/
create table j1 as
select 1 as col1 from dual;

create table j2 as
select 1 as col1 from dual union all select 2 as col1 from dual;

create table j3 as
select 3 as col1 from dual union all select 4 as col1 from dual;

create table j4 as
select 3 as col1 from dual union all select 4 as col1 from dual;

select j1.col1, j2.col1, j3.col1, j4.col1
  from j1
  full join j2
    on j2.col1 = j1.col1
  full join j3
    on j3.col1 = j1.col1
  full join j4
    on j4.col1 = j1.col1;

create table j0 as
select 1 as col0
  from dual
union all
select 2 as col0
  from dual
union all
select 3 as col0
  from dual
union all
select 4 as col0
  from dual;

select j1.col1, j2.col1, j3.col1, j4.col1
  from j0
  left join j1
    on j1.col1 = j0.col0
  left join j2
    on j2.col1 = j0.col0
  left join j3
    on j3.col1 = j0.col0
  left join j4
    on j4.col1 = j0.col0
 order by j0.col0;
/*根据传入条件返回不同列中的数据*/
create table area as
select '重庆' as 市, '沙坪坝' as 区, '小龙坎' as 镇
  from dual
union all
select '重庆' as 市, '沙坪坝' as 区, '磁器口' as 镇
  from dual
union all
select '重庆' as 市, '九龙坡' as 区, '杨家坪' as 镇
  from dual
union all
select '重庆' as 市, '九龙坡' as 区, '谢家湾' as 镇
  from dual;

var v_市 varchar2(50);
var v_区 varchar2(50);
exec :v_市:='';
exec :v_区:='九龙坡';
select distinct case
                  when :v_区 is not null then
                   镇
                  when :v_市 is not null then
                   区
                end as 地区名称
  from area
 where 市 = nvl(:v_市, 市)
   and 区 = nvl(:v_区, 区);

exec :v_市:='重庆';
exec :v_区:='';
select distinct case
                  when :v_区 is not null then
                   镇
                  when :v_市 is not null then
                   区
                end as 地区名称
  from area
 where 市 = nvl(:v_市, 市)
   and 区 = nvl(:v_区, 区);
/*拆分字符串进行连接*/
create table d_objects as select * from dba_objects;

create table test1 as
select to_char(wmsys.wm_concat(object_id)) as id_lst, owner, object_type
  from d_objects
 where owner in ('SCOTT', 'TEST')
 group by owner, object_type;

with a as
 (select id_lst, regexp_substr(id_lst, '[^,]+', 1, level) as object_id
    from test1
  connect by nocycle(prior rowid) = rowid
         and level <= length(regexp + replace(id_lst, '[^,]', ''))
         and (prior dbms_random.value) is not null)
select a.id_lst, to_char(wmsys.wm_concat(b.object_name)) as name_lst
  from a
 inner join d_objects b
    on b.object_id = a.object_id
 group by a.id_lst;
/*整理垃圾数据*/
create or replace view x0(人员编号,开始时间,结束时间,类型,数值id) as
select 11, to_date('201305', 'yyyymm'), to_date('201308', 'yyyymm'), 1, 1
  from dual
union all
select 11, to_date('201307', 'yyyymm'), null, 1, 2
  from dual
union all
select 11, to_date('201301', 'yyyymm'), null, -1, 3
  from dual
union all
select 11, to_date('201312', 'yyyymm'), null, 1, 4
  from dual
union all
select 22, to_date('201305', 'yyyymm'), to_date('201306', 'yyyymm'), 1, 1
  from dual
union all
select 22, to_date('201308', 'yyyymm'), to_date('201309', 'yyyymm'), 1, 2
  from dual
union all
select 22, to_date('201312', 'yyyymm'), to_date('201312', 'yyyymm'), -1, 3
  from dual
union all
select 22, to_date('201403', 'yyyymm'), null, 1, 4
  from dual
union all
select 22, to_date('201405', 'yyyymm'), null, -1, 4
  from dual
union all
select 33, to_date('201305', 'yyyymm'), to_date('201305', 'yyyymm'), 1, 1
  from dual
union all
select 33, to_date('201307', 'yyyymm'), to_date('201307', 'yyyymm'), 1, 2
  from dual
union all
select 33, to_date('201310', 'yyyymm'), null, -1, 3
  from dual
union all
select 33, to_date('201312', 'yyyymm'), null, 1, 4
  from dual;

create or replace view x01 as
select 人员编号,
       开始时间,
       /*当前人员的最小日期，覆盖用*/
       coalesce(min(case
                      when 类型 = -1 then
                       add_months(开始时间, -1)
                      else
                       开始时间
                    end)
                over(partition by 人员编号 order by 数值id rows between 1
                     following and unbounded following),
                开始时间 + 1) as min_开始时间,
       结束时间 as 修正前,
       /*修正结束时间*/
       case
         when 结束时间 is null and
              (lead(类型) over(partition by 人员编号 order by 数值id)) = -1 then
          add_months((lead(开始时间) over(partition by 人员编号 order by 数值id)),
                     -1)
         else
          结束时间
       end as 结束时间,
       类型,
       数值id,
       max(数值id) over(partition by 人员编号) as max_id
  from x0;
select * from x01;

create or replace view x02 as
select 人员编号,
       开始时间,
       min_开始时间,
       结束时间,
       类型,
       数值id,
       max_id,
       /*生成区间是否重叠的标识，合并时段时用*/
       case
         when (lag(结束时间) over(partition by 人员编号 order by 数值id)) <
              add_months(开始时间, -1) then
          1
         when (lag(类型) over(partition by 人员编号 order by 数值id)) = 1 then
          null
         else
          1
       end as so
  from x01;
select * from x02;

create or replace view x03 as
select 人员编号,
       数值id,
       max_id,
       类型,
       /*累加标识，生成分组合并依据*/
       sum(so) over(partition by 人员编号 order by 数值id) as so,
       开始时间,
       min_开始时间,
       /*根据最前生成的时间覆盖对应的时段*/
       case
         when min_开始时间 < 结束时间 and min_开始时间 >= 开始时间 then
          min_开始时间
         else
          结束时间
       end as 结束时间
  from x02
 where 类型 = 1
      /*如果开始时间比这还小，就丢弃吧*/
   and 开始时间 <= min_开始时间;
select * from x03;

create or replace view x04 as
select 人员编号,
       max_id,
       max(数值id) as max_id2,
       sum(类型) as 类型,
       min(开始时间) keep(dense_rank first order by 数值id) as 开始时间,
       max(结束时间) keep(dense_rank last order by 数值id) as 结束时间
  from x03
 group by 人员编号, so, max_id;
select * from x04;

create or replace view x05 as
select 人员编号,
       to_char(开始时间, 'yyyymm') || '--' ||
       coalesce(to_char(结束时间, 'yyyymm'), 'NULL') as 区间
  from x04
 where (max_id = max_id2 or 开始时间 <= 结束时间)
   and 类型 > -1;
select * from x05;
/*用“行转列”来得到隐含信息*/
create table cte as
(select 'A' as shop, '2013' as nyear, 123 as amount
  from dual
union all
select 'A' as shop, '2012' as nyear, 200 as amount
  from dual);
select * from cte;

select shop,
       max(decode(nyear, '2012', amount)),
       max(decode(nyear, '2013', amount))
  from cte
 group by shop;

select shop,
       nyear,
       max(nyear) over() as max_year,
       min(nyear) over() as min_year,
       sum(amount) as amount
  from cte
 group by shop, nyear;

with t0 as
 (select shop,
         nyear,
         /*先用分析函数做行转列，把隐藏数据提出来*/
         max(nyear) over() as max_year,
         min(nyear) over() as min_year,
         sum(amount) as amount
    from cte
   group by shop, nyear)
select shop,
       max(decode(nyear, min_year /*代替2012*/, amount)) as 去年,
       min(decode(nyear, max_year /*代替2012*/, amount)) as 今年
  from t0
 group by shop;
/*用隐藏数据进行行转列*/
select job, ename, row_number() over(partition by job order by empno) sn
  from emp;

select job,
       max(case
             when sn = 1 then
              ename
           end) as n1,
       max(case
             when sn = 2 then
              ename
           end) as n2,
       max(case
             when sn = 3 then
              ename
           end) as n3,
       max(case
             when sn = 4 then
              ename
           end) as n4
  from (select job,
               ename,
               row_number() over(partition by job order by empno) as sn
          from emp)
 group by job;

select *
  from (select job,
               ename,
               row_number() over(partition by job order by empno) as sn
          from emp)
pivot(max(ename)
   for sn in(1 as n1, 2 as n2, 3 as n3, 4 as n4));

declare
  v_max_seq number;
  v_sql     varchar2(4000);
begin
  select max(count(*)) into v_max_seq from emp group by job;
  v_sql := 'select' || chr(10);
  for i in 1 .. v_max_seq loop
    v_sql := v_sql || 'max(case when seq =' || to_char(i) ||
             'then ename end) as n' || to_char(i) || ',' || chr(10);
  end loop;
  v_sql := v_sql || 'job from(select ename,job,row_number() over(partition by job order by empno) as seq from emp)
    group by job';
  dbms_output.put_line(v_sql);
end;
/*用正则表达式提取clob里的文本格式记录集*/
select c1,
       regexp_substr(c1, '[^|#]+', 1, 1) as d1,
       regexp_substr(c1, '[^|#]+', 1, 1) as d1,
       regexp_substr(c1, '[^|#]+', 1, 2) as d2,
       regexp_substr(c1, '[^|#]+', 1, 3) as d3,
       regexp_substr(c1, '[^|#]+', 1, 4) as d4,
       regexp_substr(c1, '[^|#]+', 1, 5) as d5,
  from (select to_char(regexp_substr(c1,
                                     '[^' || chr(10) || ']+',
                                     1,
                                     level + 1)) as c1
          from test
        connect by level <= regexp_count(c1, chr(10)));
