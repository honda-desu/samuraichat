INSERT INTO roles (id, name) VALUES
(1, 'ROLE_GENERAL')
ON CONFLICT (id) DO NOTHING;

INSERT INTO roles (id, name) VALUES
(2, 'ROLE_ADMIN')
ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, name, furigana, email, password, role_id, enabled) VALUES
(1, '侍 太郎', 'サムライ タロウ', 'taro.samurai@example.com', '$2a$10$Tk2Jc373FDtcsndoLa3C9.3naEKdG5DZxOB5Xa6fZsvCTK3GAb78u', 1, true),
(2, '侍 花子', 'サムライ ハナコ', 'hanako.samurai@example.com', '$2a$10$Tk2Jc373FDtcsndoLa3C9.3naEKdG5DZxOB5Xa6fZsvCTK3GAb78u', 2, true)
ON CONFLICT (id) DO NOTHING;

INSERT INTO chat_groups (id, name) VALUES
(1, '旅行好きの集い')
ON CONFLICT (id) DO NOTHING;

INSERT INTO messages (id, user_id, chat_group_id, content, image_path, message_type, created_at) VALUES
(1, 1, 1, 'こんにちは！', NULL, 'TEXT', '2025-08-24 09:00:00'),
(2, 2, 1, 'よろしくお願いします！', NULL, 'TEXT', '2025-08-24 09:05:00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO chat_group_members (id, chat_group_id, user_id, joined_at) VALUES
(1, 1, 1, '2025-08-01 10:00:00'),
(2, 1, 2, '2025-08-01 10:05:00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO favorites (id, user_id, chat_group_id, favorited_at) VALUES
(1, 1, 1, '2025-08-01 10:00:00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO dm_room (id, user1_id, user2_id, created_at) VALUES
(1, 1, 2, '2025-08-01 09:00:00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO dm_message (id, room_id, sender_id, content, image_path, created_at, is_read) VALUES
(1, 1, 1, 'DMテスト', NULL, '2025-08-01 09:05:00', false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO blocks (id, blocker_id, blocked_user_id, created_at) VALUES
(1, 1, 2, '2025-08-01 09:10:00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO reports (id, reporter_id, target_user_id, reason, status, created_at) VALUES
(1, 2, 1, 'テスト通報', 'PENDING', '2025-08-01 09:15:00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO group_message_reads (id, message_id, user_id, read_at) VALUES
(1, 1, 2, '2025-08-24 09:20:00')
ON CONFLICT (id) DO NOTHING;