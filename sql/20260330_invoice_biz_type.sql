ALTER TABLE contract_invoice
ADD COLUMN invoice_biz_type varchar(16) DEFAULT NULL COMMENT '发票业务分类（input:进项, output:销项）' AFTER amount_type;

UPDATE contract_invoice
SET invoice_biz_type = CASE
    WHEN amount_type = '支出' THEN 'output'
    WHEN amount_type = '收入' THEN 'input'
    ELSE NULL
END
WHERE invoice_biz_type IS NULL OR invoice_biz_type = '';
