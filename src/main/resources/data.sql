-- Insert static transaction types
INSERT INTO transaction_type (id, code, name, category, description)
VALUES
  (1, 'BILL_PAYMENT', 'Bill Payment', 'BILLING', 'Pay utility or service bills'),
  (2, 'P2P_TRANSFER', 'Peer-to-Peer Transfer', 'TRANSFER', 'Transfer money to another user'),
  (3, 'MERCHANT_PAYMENT', 'Merchant Payment', 'MERCHANT', 'Pay merchants or shops'),
  (4, 'DEPOSIT', 'Deposit', 'FINANCIAL', 'Deposit funds into your account'),
  (5, 'WITHDRAWAL', 'Withdrawal', 'FINANCIAL', 'Withdraw funds from your account');

-- Insert providers for electricity, water, etc.
INSERT INTO service_providers (external_code, name, service_category_id, logo_url, description)
VALUES
('SONELGAZ', 'Sonelgaz', 1, 'http://example.com/sonelgaz.png', 'Electricity provider'),
('ADE',      'ADE',      3, 'http://example.com/ade.png',      'Water provider'),
('SEAAL',    'SEAAL',    3, 'http://example.com/seaal.png',    'Another water provider');

-- Insert categories
INSERT INTO service_categories (code, name, icon_url) VALUES
('ELECTRICITY', 'Electricity', 'http://example.com/electricity.png'),
('INTERNET', 'Internet', 'http://example.com/internet.png'),
('WATER', 'Water', 'http://example.com/water.png'),
('EWALLET', 'E-Wallet', 'http://example.com/ewallet.png'),
('ASSURANCE', 'Assurance', 'http://example.com/assurance.png'),
('SHOPPING', 'Shopping', 'http://example.com/shopping.png'),
('DEALS', 'Deals', 'http://example.com/deals.png'),
('HEALTH', 'Health', 'http://example.com/health.png');
  