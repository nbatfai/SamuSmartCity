#ifndef ROBOCAR_CAR_HPP
#define ROBOCAR_CAR_HPP

/**
 * @brief Justine - this is a rapid prototype for development of Robocar City Emulator
 *
 * @file car.hpp
 * @author  Norbert Bátfai <nbatfai@gmail.com>
 * @version 0.0.10
 *
 * @section LICENSE
 *
 * Copyright (C) 2014 Norbert Bátfai, batfai.norbert@inf.unideb.hu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @section DESCRIPTION
 * Robocar City Emulator and Robocar World Championship
 *
 * desc
 *
 */

#include <osmium/osm/types.hpp>
#include <iostream>
#include <vector>

#include <osmreader.hpp>
#include <algorithm>

#include <string>

namespace justine
{
namespace robocar
{

enum class CarType : unsigned int
{
    NORMAL = 0, POLICE, GANGSTER, CAUGHT, GOTIN
};

class Traffic;

class Car
{
public:

    Car(Traffic &traffic , CarType type = CarType::NORMAL);

    virtual void init();

    virtual void step();

    osmium::unsigned_object_id_type from() const {
        return m_from;
    }
    osmium::unsigned_object_id_type to() const {
        return m_to;
    }
    osmium::unsigned_object_id_type get_step() const {
        return m_step;
    }
    CarType get_type() const {
        return m_type;
    }
    void set_type(CarType type) {
        m_type = type;
    }

    osmium::unsigned_object_id_type to_node() const;
    osmium::unsigned_object_id_type get_max_steps() const;
    virtual void nextEdge(void);
    virtual void nextSmarterEdge(void);

    virtual void print(std::ostream &os) const {

        os << m_from
           << " "
           << to_node()
           << " "
           << get_max_steps()
           << " "
           << get_step()
           << " "
           << static_cast<unsigned int>(get_type());

    }

    friend std::ostream &operator<< (std::ostream &os, Car &c) {

        c.print(os);

        return os;

    }


protected:
    Traffic &traffic;
    CarType m_type {CarType::NORMAL};
    osmium::unsigned_object_id_type m_from {3130863972};
    osmium::unsigned_object_id_type m_to {0};
    osmium::unsigned_object_id_type m_step {0};

private:

};

class AntCar : public Car
{
public:
    AntCar(Traffic &traffic);

    virtual void nextSmarterEdge(void);

    virtual void print(std::ostream &os) const {

        os << m_from
           << " "
           << to_node()
           << " "
           << get_max_steps()
           << " "
           << get_step()
           << " "
           << static_cast<unsigned int>(get_type());

    }

    osmium::unsigned_object_id_type ant(void);
    osmium::unsigned_object_id_type ant_rnd(void);
    osmium::unsigned_object_id_type ant_rernd(void);
    osmium::unsigned_object_id_type ant_mrernd(void);

    static AdjacencyList alist;
    static AdjacencyList alist_evaporate;


private:
    bool rnd {true};

};


class SmartCar : public Car
{
public:
    SmartCar(Traffic &traffic, CarType type, bool guided);

    virtual void step();
    virtual void init();

    virtual void print(std::ostream &os) const {

        os << m_from
           << " "
           << to_node()
           << " "
           << get_max_steps()
           << " "
           << get_step()
           << " "
           << static_cast<unsigned int>(get_type());

    }

    bool get_guided() const {
        return m_guided;
    }
    bool set_route(std::vector<unsigned int> &route);
    virtual void nextEdge(void);
    virtual void nextGuidedEdge(void);
    bool set_fromto(unsigned int from, unsigned int to);

    void set_from(osmium::unsigned_object_id_type f) {
        m_from = f;
    }
    void set_to(osmium::unsigned_object_id_type t) {
        m_to = t;
    }
    void set_step(osmium::unsigned_object_id_type s) {
        m_step = s;
    }

private:
    bool m_guided {false};
    bool m_routed {false};

    std::vector<unsigned int> route;

};

class CopCar : public SmartCar
{
public:
    CopCar(Traffic &traffic, bool guided, const char *name);

    virtual void print(std::ostream &os) const {

        os << m_from
           << " "
           << to_node()
           << " "
           << get_max_steps()
           << " "
           << get_step()
           << " "
           << static_cast<unsigned int>(get_type())
           << " "
           << get_num_captured_gangsters()
           << " "
           << m_name;

    }

    std::string get_name() const {
        return m_name;
    }

    bool is_passenger() const {
        return isPassenger;
    }

    std::shared_ptr<SmartCar> gotOut() {

        ++m_num_captured_gangsters;

        isPassenger = false;
        dest_from = 0;

        std::string free("full");
        int i = m_name.find(free);

        if (i != std::string::npos)
            m_name.replace(i, free.length(), "free");

        return passenger;
    }

    int get_num_captured_gangsters() const {
        return m_num_captured_gangsters;
    }

    void captured_gangster(std::shared_ptr<SmartCar> &passenger);

    osmium::unsigned_object_id_type dfrom() const {
        return dest_from;
    }
    osmium::unsigned_object_id_type dto() const {
        return dest_to;
    }
    osmium::unsigned_object_id_type dfrom(osmium::unsigned_object_id_type df) {
        dest_from = df;
    }
    osmium::unsigned_object_id_type dto(osmium::unsigned_object_id_type dt) {
        dest_to = dt;
    }

protected:

    int m_num_captured_gangsters {0};
    std::string m_name;
    std::shared_ptr<SmartCar> passenger {nullptr};
    bool isPassenger {false};

    osmium::unsigned_object_id_type dest_from {0/*2909260989*/};
    osmium::unsigned_object_id_type dest_to {0/*2909261503*/};

    int number;
    static int license_plate_number;
};

}
} // justine::robocar::


#endif // ROBOCAR_CAR_HPP

