export function getInvoiceBizType(amountType) {
  if (amountType === '支出') {
    return {
      key: 'output',
      label: '销项',
      rawLabel: '支出',
      fullLabel: '销项（支出）',
      tagType: 'danger'
    }
  }
  if (amountType === '收入') {
    return {
      key: 'input',
      label: '进项',
      rawLabel: '收入',
      fullLabel: '进项（收入）',
      tagType: 'success'
    }
  }
  return {
    key: 'unknown',
    label: '-',
    rawLabel: amountType || '-',
    fullLabel: amountType || '-',
    tagType: 'info'
  }
}

export function getInvoiceTabAmountType(tab) {
  if (tab === 'input') return '收入'
  if (tab === 'output') return '支出'
  return null
}

export function getInvoiceDialogTitle(amountType) {
  const bizType = getInvoiceBizType(amountType)
  if (bizType.key === 'output') return '录销项发票'
  if (bizType.key === 'input') return '录进项发票'
  return '录发票'
}
