-- ================================================
-- Создание последовательностей для генерации ID
-- ================================================
CREATE SEQUENCE IF NOT EXISTS filter_id_seq START 1;
CREATE SEQUENCE IF NOT EXISTS link_id_seq START 1;
CREATE SEQUENCE IF NOT EXISTS tag_id_seq START 1;

-- ================================================
-- Таблица чатов
-- ================================================
CREATE TABLE IF NOT EXISTS chats (
    chat_id BIGINT PRIMARY KEY
);

-- ================================================
-- Таблица ссылок
-- ================================================
CREATE TABLE IF NOT EXISTS links (
    link_id BIGINT PRIMARY KEY DEFAULT nextval('link_id_seq'),
    url TEXT NOT NULL,
    last_modified TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ================================================
-- Связующая таблица для Many-to-Many (чаты и ссылки)
-- ================================================
CREATE TABLE IF NOT EXISTS chat_links (
    chat_id BIGINT,
    link_id BIGINT,
    PRIMARY KEY (chat_id, link_id),
    FOREIGN KEY (chat_id) REFERENCES chats(chat_id) ON DELETE CASCADE,
    FOREIGN KEY (link_id) REFERENCES links(link_id) ON DELETE CASCADE
);

-- ================================================
-- Таблица фильтров
-- ================================================
CREATE TABLE IF NOT EXISTS filters (
    filter_id BIGINT PRIMARY KEY DEFAULT nextval('filter_id_seq'),
    chat_id BIGINT,
    link_id BIGINT,
    filter TEXT NOT NULL,
    FOREIGN KEY (chat_id) REFERENCES chats(chat_id) ON DELETE CASCADE,
    FOREIGN KEY (link_id) REFERENCES links(link_id) ON DELETE CASCADE
);

-- ================================================
-- Таблица тегов
-- ================================================
CREATE TABLE IF NOT EXISTS tags (
    tag_id BIGINT PRIMARY KEY DEFAULT nextval('tag_id_seq'),
    chat_id BIGINT,
    link_id BIGINT,
    tag TEXT NOT NULL,
    FOREIGN KEY (chat_id) REFERENCES chats(chat_id) ON DELETE CASCADE,
    FOREIGN KEY (link_id) REFERENCES links(link_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_chats_chat_id ON chats(chat_id);

CREATE INDEX IF NOT EXISTS idx_links_link_id on links(link_id);
CREATE INDEX IF NOT EXISTS idx_links_url on links(url);

CREATE INDEX IF NOT EXISTS idx_chat_links_chat_id ON chat_links(chat_id);
CREATE INDEX IF NOT EXISTS idx_chat_links_link_id ON chat_links(link_id);

CREATE INDEX IF NOT EXISTS idx_tags_chat_id ON tags(chat_id);
CREATE INDEX IF NOT EXISTS idx_tags_chat_id ON tags(link_id);
CREATE INDEX IF NOT EXISTS idx_tags_tag ON tags(tag);

CREATE INDEX IF NOT EXISTS idx_filters_chat_id ON filters(chat_id);
CREATE INDEX IF NOT EXISTS idx_filters_chat_id ON filters(link_id);
CREATE INDEX IF NOT EXISTS idx_filters_filter ON filters(filter);
