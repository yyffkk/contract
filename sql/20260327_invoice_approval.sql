-- 发票管理审批流字段补充
ALTER TABLE contract_invoice
    ADD COLUMN approval_status varchar(32) DEFAULT 'draft' COMMENT '审批状态 draft/pending/approved/rejected',
    ADD COLUMN approver varchar(255) NULL COMMENT '审批人',
    ADD COLUMN cc varchar(255) NULL COMMENT '抄送人',
    ADD COLUMN submitter varchar(64) NULL COMMENT '提交审批人',
    ADD COLUMN submit_time datetime NULL COMMENT '提交审批时间',
    ADD COLUMN approve_time datetime NULL COMMENT '审批完成时间';

UPDATE contract_invoice
SET approval_status = IFNULL(approval_status, 'draft')
WHERE approval_status IS NULL;
