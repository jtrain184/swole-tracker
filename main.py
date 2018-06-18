# Copyright 2016 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import webapp2
import datetime
import json

from google.appengine.ext import ndb

# I used this to allow PATCH request, it would not work otherwise
# https://bit.ly/2KotiVE
allowed_methods = webapp2.WSGIApplication.allowed_methods
new_allowed_methods = allowed_methods.union(('PATCH',))
webapp2.WSGIApplication.allowed_methods = new_allowed_methods


# Entity Models
class Wimp(ndb.Model):
    name = ndb.StringProperty()
    sex = ndb.StringProperty()
    weight = ndb.IntegerProperty()
    age = ndb.IntegerProperty()
    id = ndb.StringProperty()

class Workout(ndb.Model):
    name = ndb.StringProperty()
    reps = ndb.IntegerProperty()
    duration = ndb.IntegerProperty()
    weight = ndb.IntegerProperty()
    user =  ndb.StringProperty()
    id = ndb.StringProperty()

class MainPage(webapp2.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'text/plain'
        current_time = datetime.datetime.now()
        self.response.write("Current Server Time:  " + current_time.strftime("%Y-%m-%d %H:%M"))

class WimpListHandler(webapp2.RequestHandler):
    def get(self, id=None):
        self.response.headers['Content-Type'] = 'application/json'
        wimp_list = []
        wimp_query =  Wimp.query().fetch()
        for wimp in wimp_query:
            wimp_dict = wimp.to_dict()
            wimp_dict['self'] = '/wimps/' + wimp.id
            wimp_list.append(wimp_dict)

        self.response.write(json.dumps(wimp_list))

    def post(self):
        wimp_data =  json.loads(self.request.body)
   
        # Check for unique wimp name
        name_query = Wimp.query(Wimp.name == wimp_data['name'])
        name_count = name_query.count()
        if name_count > 0:
            self.response.set_status(404)
            self.response.write("Error:  User name already exits")
        else:
            NewWimp = Wimp(
                name=wimp_data["name"], sex=wimp_data["sex"], weight=wimp_data["weight"], age=wimp_data["age"]
            )
            wimp_key = NewWimp.put()

            # Grab id and store it as a string
            wimp_id = wimp_key.id()
            NewWimp.id = str(wimp_id)
            NewWimp.put()

            # Convert to dict for output
            wimp_dict = NewWimp.to_dict()
            wimp_dict['self'] = '/wimps/' + str(wimp_id)
            self.response.set_status(201)
            self.response.write(json.dumps(wimp_dict))
    

class WimpHandler(webapp2.RequestHandler):
    # Retrieve a single wimp
    def get(self, id=None):
        if id:
            wimp = Wimp.get_by_id(int(id))
            if wimp is None:
                self.response.set_status(404)
                self.response.write("Error:  Wimp not found")
            else:
                wimp_dict = wimp.to_dict()
                wimp_dict['self'] = '/wimps/' + id
                self.response.write(json.dumps(wimp_dict))

    # Modify a wimp
    def patch(self, id=None):
        if id:
            wimp = Wimp.get_by_id(int(id))
            if wimp is None:
                self.response.set_status(404)
                self.response.write("Error:  Wimp not found")
            else:
                # Check request for valid data and update wimp
                wimp_data = json.loads(self.request.body)
                if 'name' in wimp_data and type(wimp_data['name']) is unicode:
                    wimp.name = wimp_data['name']
                if 'sex' in wimp_data and type(wimp_data['sex']) is unicode:
                    wimp.sex = wimp_data['sex']
                if 'weight' in wimp_data and type(wimp_data['weight']) is int:
                    wimp.weight = wimp_data['weight']
                if 'age' in wimp_data and type(wimp_data['age']) is int:
                    wimp.age = wimp_data['age']

                # Commit to the db
                wimp.put()

                # Return updated wimp as response
                wimp_dict = wimp.to_dict()
                wimp_dict['self'] = '/wimps/' + id
                self.response.write(json.dumps(wimp_dict))


    # DELETE a wimp and any associated workouts, if needed
    def delete(self, id=None):
        if id:
                wimp = Wimp.get_by_id(int(id))
                if wimp is None:
                    self.response.set_status(404)
                    self.response.write("Error:  Wimp not found")
                    return
                wimp_id = id

                # Delete assocaiated workouts
                workout_query = Workout.query(Workout.user == wimp_id).fetch()
                if workout_query is not None:
                    for workout in workout_query:
                        workout.key.delete()
                
                # Delete the wimp
                wimp.key.delete()


class WorkoutListHandler(webapp2.RequestHandler):
    def get(self, id=None):
        self.response.headers['Content-Type'] = 'application/json'
        workout_list = []
        workout_query =  Workout.query().fetch()
        for workout in workout_query:
            workout_dict = workout.to_dict()
            workout_dict['self'] = '/workouts/' + str(workout.id)
            workout_list.append(workout_dict)

        self.response.write(json.dumps(workout_list))

    def post(self):
        workout_data =  json.loads(self.request.body)
        new_workout = Workout(
            name=workout_data["name"],reps=workout_data["reps"],duration=workout_data["duration"],
            weight=workout_data["weight"],user=workout_data["user"]
        )
        workout_key = new_workout.put()

        # Grab id and store it as a string
        workout_id = workout_key.id()
        new_workout.id = str(workout_id)
        new_workout.put()

        workout_dict = new_workout.to_dict()
        workout_dict['self'] = '/workouts/' + str(workout_id)
        self.response.set_status(201)
        self.response.write(json.dumps(workout_dict))

class WorkoutHandler(webapp2.RequestHandler):
    # Retrieve a single workout
    def get(self, id=None):
        if id:
            workout = Workout.get_by_id(int(id))
            if workout is None:
                self.response.set_status(404)
                self.response.write("Error:  Workout not found")
            else:
                workout_dict = workout.to_dict()
                workout_dict['self'] = '/workouts/' + id
                self.response.write(json.dumps(workout_dict))

    # Modify a workout
    def patch(self, id=None):
        if id:
            workout = Workout.get_by_id(int(id))
            if workout is None:
                self.response.set_status(404)
                self.response.write("Error:  Workout not found")
            else:
                # Check request for valid data and update wimp
                workout_data = json.loads(self.request.body)
                if 'name' in workout_data and type(workout_data['name']) is unicode:
                    workout.name = workout_data['name']
                if 'reps' in workout_data and type(workout_data['reps']) is int:
                    workout.reps = workout_data['reps']
                if 'duration' in workout_data and type(workout_data['duration']) is int:
                    workout.duration = workout_data['duration']
                if 'weight' in workout_data and type(workout_data['weight']) is int:
                    workout.weight = workout_data['weight']

                workout.put()
                # Return updated workout as response
                workout_dict = workout.to_dict()
                workout_dict['self'] = '/workouts/' + id
                self.response.write(json.dumps(workout_dict))

    # DELETE a workout
    def delete(self, id=None):
        if id:
            workout = Workout.get_by_id(int(id))
            if workout is None:
                self.response.set_status(404)
                self.response.write("Error:  Workout not found")
                return
            workout.key.delete()
            workout_dict = workout.to_dict()
            self.response.write(json.dumps(workout_dict))

class WimpWorkoutsHandler(webapp2.RequestHandler):
    # Retrieve a single wimp
    def get(self, id=None):
        self.response.headers['Content-Type'] = 'application/json'
        if id:
            wimp_id = str(id)
            workout_list = []
            workout_query =  Workout.query(Workout.user == wimp_id).fetch()
            for workout in workout_query:
                workout_dict = workout.to_dict()
                workout_list.append(workout_dict)

            self.response.write(json.dumps(workout_list))


# TO CLEAR DATABASE, NOT FOR "PRODUCTION"

class ClearHandler(webapp2.RequestHandler):
    def delete(self, id=None):
        wimp_query = Wimp.query().fetch()
        if wimp_query is not None:
            for wimp in wimp_query:
                wimp.key.delete()

        workout_query = Workout.query().fetch()
        if workout_query is not None:
            for workout in workout_query:
                workout.key.delete()

        self.response.write("Wimps and workouts have been cleared")

app = webapp2.WSGIApplication([
    ('/', MainPage),
    ('/wimps', WimpListHandler),
    ('/workouts', WorkoutListHandler),
    ('/wimps/(.*)', WimpHandler),
    ('/workouts/(.*)', WorkoutHandler),
    ('/userworkouts/(.*)', WimpWorkoutsHandler),
    ('/clear', ClearHandler),
], debug=True)
