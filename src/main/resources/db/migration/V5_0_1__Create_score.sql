DROP TABLE IF EXISTS t_company_info;
CREATE TABLE t_company_info (
  id     INT(11)   NOT NULL AUTO_INCREMENT
  COMMENT 'id',
  c_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
  COMMENT '创建时间',
  PRIMARY KEY (id)
)
  DEFAULT CHARSET = utf8
  COMMENT '企业信息表';

DROP TABLE IF EXISTS t_person_info;
CREATE TABLE t_person_info (
  id     INT(11)   NOT NULL AUTO_INCREMENT
  COMMENT 'id',
  c_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
  COMMENT '创建时间',
  PRIMARY KEY (id)
)
  DEFAULT CHARSET = utf8
  COMMENT '申请人信息表';

DROP TABLE IF EXISTS t_material_info;
CREATE TABLE t_material_info (
  id          INT(11)      NOT NULL AUTO_INCREMENT
  COMMENT 'id',
  name        VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '材料名',
  note        VARCHAR(255)          DEFAULT ''
  COMMENT '备注',
  create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
  COMMENT '创建时间',
  add_user    VARCHAR(64)           DEFAULT ''
  COMMENT '创建用户',
  PRIMARY KEY (id)
)
  DEFAULT CHARSET = utf8
  COMMENT '材料信息表';

DROP TABLE IF EXISTS t_indicator;
CREATE TABLE t_indicator (
  id          INT(11)      NOT NULL AUTO_INCREMENT
  COMMENT 'id',
  category    VARCHAR(16)  NOT NULL DEFAULT ''
  COMMENT '类别',
  index_num   INT(11)      NOT NULL DEFAULT 0
  COMMENT '序号',
  name        VARCHAR(64)  NOT NULL DEFAULT ''
  COMMENT '指标',
  note        VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '备注',
  item_type   INT(11)      NOT NULL DEFAULT 0
  COMMENT '选项类型  0：单选题，1：填空题',
  create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
  COMMENT '创建时间',
  add_user    VARCHAR(64)           DEFAULT ''
  COMMENT '创建用户',
  PRIMARY KEY (id)
)
  DEFAULT CHARSET = utf8
  COMMENT '指标信息表';

DROP TABLE IF EXISTS t_relate_indicator_material;
CREATE TABLE t_relate_indicator_material (
  indicator_id INT(11) NOT NULL DEFAULT 0
  COMMENT '指标ID',
  material_id  INT(11) NOT NULL DEFAULT 0
  COMMENT '材料ID',
  PRIMARY KEY (indicator_id, material_id)
)
  DEFAULT CHARSET = utf8
  COMMENT '指标材料关联表';

DROP TABLE IF EXISTS t_indicator_item;
CREATE TABLE t_indicator_item (
  id             INT(11)      NOT NULL AUTO_INCREMENT
  COMMENT 'id',
  indicator_id   INT(11)      NOT NULL DEFAULT 0
  COMMENT '指标信息ID',
  content        VARCHAR(255) NOT NULL DEFAULT ''
  COMMENT '选项内容',
  score          INT(11)               DEFAULT 0
  COMMENT '分数',
  score_function VARCHAR(255)          DEFAULT ''
  COMMENT '分数计算',
  c_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
  COMMENT '创建时间',
  PRIMARY KEY (id)
)
  DEFAULT CHARSET = utf8
  COMMENT '指标信息选项表';

DROP TABLE IF EXISTS t_relate_indicator_role;
CREATE TABLE t_relate_indicator_role (
  indicator_id INT(11) NOT NULL DEFAULT 0
  COMMENT '指标ID',
  role_id      INT(11) NOT NULL DEFAULT 0
  COMMENT '角色ID',
  PRIMARY KEY (indicator_id, role_id)
)
  DEFAULT CHARSET = utf8
  COMMENT '指标角色关联表';


DROP TABLE IF EXISTS t_person_score_info;
CREATE TABLE t_person_score_info (
  id             INT(11)     NOT NULL  AUTO_INCREMENT
  COMMENT 'id',
  accept_number  VARCHAR(32) NOT NULL  DEFAULT ''
  COMMENT '受理编号',
  indicator_id   INT(11)     NOT NULL  DEFAULT 0
  COMMENT '指标ID',
  indicator_name VARCHAR(64)           DEFAULT ''
  COMMENT '指标名称',
  person_id      INT(11)     NOT NULL  DEFAULT 0
  COMMENT '申请人ID',
  person_name    VARCHAR(64) NOT NULL  DEFAULT ''
  COMMENT '申请人',
  person_id_num  VARCHAR(32) NOT NULL  DEFAULT ''
  COMMENT '申请人身份证',
  company_id     INT(11)               DEFAULT 0
  COMMENT '企业ID',
  commany_name   VARCHAR(128)          DEFAULT ''
  COMMENT '企业名称',
  status         INT(11)               DEFAULT 0
  COMMENT '办理进度',
  score          INT(11)               DEFAULT 0
  COMMENT '打分',
  accept_date    DATE                  DEFAULT '0000-00-00'
  COMMENT '受理日期',
  submit_date    DATE                  DEFAULT '0000-00-00'
  COMMENT '送达日期',
  score_date     DATE                  DEFAULT '0000-00-00'
  COMMENT '打分日期',
  op_user_id     INT(11)               DEFAULT 0
  COMMENT '审核人id',
  op_user        VARCHAR(64)           DEFAULT ''
  COMMENT '审核人',
  op_role_id     INT(11)               DEFAULT 0
  COMMENT '审核部门id',
  op_role        VARCHAR(64)           DEFAULT ''
  COMMENT '审核部门',
  score_detail   VARCHAR(255)          DEFAULT ''
  COMMENT '打分说明',
  c_time         TIMESTAMP   NOT NULL  DEFAULT CURRENT_TIMESTAMP
  COMMENT '创建时间',
  PRIMARY KEY (id)
)
  DEFAULT CHARSET = utf8
  COMMENT '申请人打分信息表';

DROP TABLE IF EXISTS t_person_material_accept;
CREATE TABLE t_person_material_accept (
  id          INT(11)   NOT NULL AUTO_INCREMENT
  COMMENT 'id',
  batch_id    INT(11)   NOT NULL DEFAULT 0
  COMMENT '批次ID',
  person_id   INT(11)   NOT NULL DEFAULT 0
  COMMENT '申请人ID',
  role_id     INT(11)   NOT NULL DEFAULT 0
  COMMENT '部门ID',
  material_id INT(11)   NOT NULL DEFAULT 0
  COMMENT '材料ID',
  c_time      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
  COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY (batch_id, person_id, role_id, material_id)
)
  DEFAULT CHARSET = utf8
  COMMENT '申请人材料送达记录表';

DROP TABLE IF EXISTS t_online_person_material;
CREATE TABLE t_online_person_material (
  id            INT(11)      NOT NULL AUTO_INCREMENT
  COMMENT 'id',
  person_id     INT(11)      NOT NULL DEFAULT 0
  COMMENT '申请人ID',
  batch_id      INT(11)      NOT NULL DEFAULT 0
  COMMENT '批次ID',
  material_id   INT(11)      NOT NULL DEFAULT 0
  COMMENT '材料ID',
  material_name VARCHAR(128) NOT NULL DEFAULT ''
  COMMENT '材料名称',
  material_uri  VARCHAR(1024)         DEFAULT ''
  COMMENT '材料uri',
  status        INT(11)      NOT NULL DEFAULT 0
  COMMENT '材料状态',
  c_time        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
  COMMENT '创建时间',
  PRIMARY KEY (id)
)
  DEFAULT CHARSET = utf8
  COMMENT '申请人线上材料信息表';

DROP TABLE IF EXISTS t_batch_conf;
CREATE TABLE t_batch_conf (
  id                INT(11)     NOT NULL AUTO_INCREMENT
  COMMENT 'id',
  batch_name        VARCHAR(32) NOT NULL DEFAULT ''
  COMMENT '批次名称：2018年1期',
  batch_number      VARCHAR(32) NOT NULL DEFAULT ''
  COMMENT '批次号：201811',
  apply_begin       DATE                 DEFAULT '0000-00-00'
  COMMENT '在线申请开始日期',
  apply_end         DATE                 DEFAULT '0000-00-00'
  COMMENT '在线申请结束日期',
  accept_begin      DATE                 DEFAULT '0000-00-00'
  COMMENT '受理开始日期',
  accept_end        DATE                 DEFAULT '0000-00-00'
  COMMENT '受理结束日期',
  accept_address_id INT(11)     NOT NULL DEFAULT 0
  COMMENT '受理地点',
  indicator_type    INT(11)     NOT NULL DEFAULT 0
  COMMENT '指标方式 0:总人数选取 1:分数线选取',
  indicator_value   INT(11)              DEFAULT 0
  COMMENT '指标值',
  accept_user_count INT(11)              DEFAULT 0
  COMMENT '设置受理人数',
  PRIMARY KEY (id)
)
  DEFAULT CHARSET = utf8
  COMMENT '受理预约批次设置表';

DROP TABLE IF EXISTS t_accept_date_conf;
CREATE TABLE t_accept_date_conf (
  id                 INT(11)    NOT NULL AUTO_INCREMENT
  COMMENT 'id',
  batch_id           INT(11)    NOT NULL DEFAULT 0
  COMMENT '批次ID',
  accept_date        DATE       NOT NULL DEFAULT '0000-00-00'
  COMMENT '受理日期',
  week_day           VARCHAR(8) NOT NULL DEFAULT ''
  COMMENT '周几',
  am_user_count      INT(11)             DEFAULT 0
  COMMENT '上午发放人数',
  pm_user_count      INT(11)             DEFAULT 0
  COMMENT '下午发放人数',
  am_remaining_count INT(11)             DEFAULT 0
  COMMENT '上午剩余人数',
  pm_remaining_count INT(11)             DEFAULT 0
  COMMENT '下午剩余人数',
  PRIMARY KEY (id)
)
  DEFAULT CHARSET = utf8
  COMMENT '受理预约批次日发放设置表';


DROP TABLE IF EXISTS t_basic_conf;
CREATE TABLE t_basic_conf (
  id INT(11) NOT NULL AUTO_INCREMENT
  COMMENT 'id',
  PRIMARY KEY (id)
)
  DEFAULT CHARSET = utf8
  COMMENT '基本设置表';


DROP TABLE IF EXISTS score_server.t_person_accept_info;
CREATE TABLE score_server.t_person_accept_info (
  id                  INT(11)      NOT NULL AUTO_INCREMENT
  COMMENT '主键ID',
  batch_id            INT(11)      NOT NULL DEFAULT '0'
  COMMENT '批次ID',
  person_info_id      INT(11)      NOT NULL DEFAULT '0'
  COMMENT '申请人身份信息id',
  accept_number       VARCHAR(64)  NOT NULL DEFAULT ''
  COMMENT '受理编号',
  accept_address_id   INT(11)      NOT NULL DEFAULT '0'
  COMMENT '受理地点1、市级行政许可中心，2、滨海新区行政服务中心',
  accept_address      VARCHAR(128) NOT NULL DEFAULT ''
  COMMENT '受理地点',
  reservaion_date     INT(11)      NOT NULL DEFAULT '0'
  COMMENT '预约日期',
  reservaion_m        INT(11)      NOT NULL DEFAULT '0'
  COMMENT '上午，下午',
  status              INT(11)      NOT NULL DEFAULT '0'
  COMMENT '状态，1、信息保存，2、测评未通过，3、测评通过，4、待审核，5、审核未通过，6、审核通过，7、补充上传',
  website_review_type INT(11)      NOT NULL DEFAULT '0'
  COMMENT '网上预审类型：1、通过、2、补件、3、当期不合格',
  website_review_time INT(11)      NOT NULL DEFAULT '0'
  COMMENT '网上预审时间',
  police_review_type  INT(11)      NOT NULL DEFAULT '0'
  COMMENT '公安审核类型，1、审核通过；2、审核不通过',
  police_review_time  INT(11)      NOT NULL DEFAULT '0'
  COMMENT '公安审核时间',
  pre_review_status   INT(11)      NOT NULL DEFAULT '0'
  COMMENT '前置预审状态；1、审核通过，2、审核不通过',
  pre_review_time     INT(11)      NOT NULL DEFAULT '0'
  COMMENT '前置预审时间',
  formal_accept_time  INT(11)      NOT NULL DEFAULT '0'
  COMMENT '人社正式受理时间',
  file_send_status    INT(11)      NOT NULL DEFAULT '0'
  COMMENT '材料送达状态1、已送达，2、未送达',
  socore_status       INT(11)      NOT NULL DEFAULT '0'
  COMMENT '打分状态，1、未打分，2、已打分',
  c_time              TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
  COMMENT '创建时间',
  PRIMARY KEY (id)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COMMENT ='本期受理人员信息表';









