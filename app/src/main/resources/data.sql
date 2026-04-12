INSERT INTO admin (email, password)
SELECT 'admin@email.com', 'admin'
WHERE NOT EXISTS (
  SELECT 1 FROM admin WHERE email = 'admin@email.com'
);
