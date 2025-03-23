-- Insert static transaction types
INSERT INTO transaction_type (id, code, name, category, description)
VALUES
  (1, 'BILL_PAYMENT', 'Bill Payment', 'BILLING', 'Pay utility or service bills'),
  (2, 'P2P_TRANSFER', 'Peer-to-Peer Transfer', 'TRANSFER', 'Transfer money to another user'),
  (3, 'MERCHANT_PAYMENT', 'Merchant Payment', 'MERCHANT', 'Pay merchants or shops'),
  (4, 'DEPOSIT', 'Deposit', 'FINANCIAL', 'Deposit funds into your account'),
  (5, 'WITHDRAWAL', 'Withdrawal', 'FINANCIAL', 'Withdraw funds from your account');

-- Insert static billers
INSERT INTO billers (id, external_code, name, category, logo_url, description)
VALUES
  (1, 'ELEC_COMPANY', 'Electricity Company', 'UTILITY', 'https://example.com/logo.png', 'Pay your electricity bills'),
  (2, 'WATER_CO', 'Water Utility', 'UTILITY', 'https://example.com/logo2.png', 'Pay your water bills'),
  (3, 'INTERNET_PROVIDER', 'Internet Provider', 'UTILITY', 'https://example.com/logo3.png', 'Pay your internet bills');
