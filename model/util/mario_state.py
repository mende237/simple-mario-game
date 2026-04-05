import json
from enum import Enum


with open('../../const/MarioState.json', 'r') as file:
    data = json.load(file)

MarioState = Enum("MarioState", data)