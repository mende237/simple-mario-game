import grpc
from concurrent import futures
import time

import data_pb2
import data_pb2_grpc

_ONE_DAY_IN_SECONDS = 60 * 60 * 24

class GameServiceServicer(data_pb2_grpc.GameServiceServicer):
    def GetAction(self, request, context):
        # For now, just return a default action.
        # In a real scenario, this would contain AI logic.
        print(f"Received GameData: Mario at ({request.mario.x}, {request.mario.y}, {request.mario.state})")
        for antagonist in request.antagonists:
            print(f"  Antagonist: {antagonist.name} at ({antagonist.x}, {antagonist.y})")
        for item in request.items:
            print(f"  Item: {item.name} at ({item.x}, {item.y})")

        return data_pb2.Action(action=0) # Example: 0 could mean "do nothing" or "move right"

def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    data_pb2_grpc.add_GameServiceServicer_to_server(GameServiceServicer(), server)
    server.add_insecure_port('[::]:50051')
    server.start()
    print("Server started, listening on port 50051")
    try:
        while True:
            time.sleep(_ONE_DAY_IN_SECONDS)
    except KeyboardInterrupt:
        server.stop(0)

if __name__ == '__main__':
    serve()
