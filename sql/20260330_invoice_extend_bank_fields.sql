ALTER TABLE `contract_invoice`
  ADD COLUMN `bank_name` varchar(200) DEFAULT NULL COMMENT '开户银行' AFTER `purchaser_tax_no`,
  ADD COLUMN `bank_account` varchar(100) DEFAULT NULL COMMENT '银行账号' AFTER `bank_name`,
  ADD COLUMN `bank_address` varchar(255) DEFAULT NULL COMMENT '地址' AFTER `bank_account`,
  ADD COLUMN `bank_phone` varchar(50) DEFAULT NULL COMMENT '电话' AFTER `bank_address`,
  ADD COLUMN `invoice_content` varchar(500) DEFAULT NULL COMMENT '开票内容' AFTER `seller_tax_no`,
  ADD COLUMN `untaxed_amount` decimal(18,2) DEFAULT NULL COMMENT '不含税金额' AFTER `invoice_content`;
