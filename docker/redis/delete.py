import redis

def delete_all_redis_keys():
    try:
        # Connect to Redis
        r = redis.Redis(host='localhost', port=6379, password=None)
        
        # Get all keys
        keys = r.keys('*')
        
        if keys:
            print(f"Found {len(keys)} keys in Redis. Deleting all keys...")
            # Delete all keys
            r.delete(*keys)
            print(f"Deleted {len(keys)} keys.")
        else:
            print("No keys found in Redis.")
        
    except redis.ConnectionError as e:
        print(f"Redis connection error: {e}")

if __name__ == "__main__":
    delete_all_redis_keys()
