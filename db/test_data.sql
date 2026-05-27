INSERT INTO mc_export_test (user_name, email, phone, status, amount, description)
SELECT
    CONCAT('用户', n) as user_name,
    CONCAT('user', n, '@test.com') as email,
    CONCAT('138', LPAD(n, 8, '0')) as phone,
    (n % 3) as status,
    (n * 10.5) as amount,
    CONCAT('测试描述_', n) as description
FROM (
    SELECT @row := @row + 1 as n
    FROM (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
         (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
         (SELECT 0 UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4) c,
         (SELECT @row := 0) r
    LIMIT 10000
) nums;