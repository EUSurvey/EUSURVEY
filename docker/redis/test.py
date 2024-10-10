import redis

def test_redis_connection():
    try:
       
        r = redis.Redis(host='localhost', port=6379, password=None)
        r.set('test_key', 'Hello, Redis!')
        value = r.get('test_key')

        print(f"Stored value: {value.decode('utf-8')}")

        if r.ping():
            print("Successfully connected to Redis!")
        else:
            print("Failed to connect to Redis!")

    except redis.ConnectionError as e:
        print(f"Redis connection error: {e}")

if __name__ == "__main__":
    test_redis_connection()

