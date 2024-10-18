import redis

def browse_http_sessions():
    try:
        r = redis.Redis(host='localhost', port=6379, password=None)
        keys = r.keys('*')
        
        if keys:
            print(f"Found {len(keys)} entries in Redis:")
            for key in keys:
                key_type = r.type(key).decode('utf-8')
                print(f"Key: {key.decode('utf-8')}, Type: {key_type}", end='')
                if key_type == 'string':
                    value = r.get(key)
                    print(f", Value: {value.decode('utf-8') if value else 'None'}")
                else:
                    print(", Value: <non-string data>")
        else:
            print("No entries found in Redis.")
        
    except redis.ConnectionError as e:
        print(f"Redis connection error: {e}")

if __name__ == "__main__":
    browse_http_sessions()
