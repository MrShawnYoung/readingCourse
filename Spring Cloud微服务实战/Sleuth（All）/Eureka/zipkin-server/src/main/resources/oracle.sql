CREATE TABLE zipkin_spans (
  trace_id number NOT NULL,
  id number NOT NULL,
  name VARCHAR(255) NOT NULL,
  parent_id number,
  debug number(1),
  start_ts number,
  duration number
);

CREATE TABLE zipkin_annotations (
  trace_id number NOT NULL,
  span_id number NOT NULL,
  a_key VARCHAR(255) NOT NULL,
  a_value BLOB,
  a_type INT NOT NULL,
  a_timestamp number,
  endpoint_ipv4 number,
  endpoint_port number,
  endpoint_service_name VARCHAR(255)
)